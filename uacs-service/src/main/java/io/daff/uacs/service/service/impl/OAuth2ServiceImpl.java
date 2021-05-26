package io.daff.uacs.service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import io.daff.consts.SystemConstants;
import io.daff.enums.Hint;
import io.daff.exception.BaseException;
import io.daff.exception.InconsistentDataException;
import io.daff.exception.InsufficientPermissionsException;
import io.daff.exception.NoSuchDataException;
import io.daff.exception.ParamMissException;
import io.daff.exception.ParamValidateException;
import io.daff.exception.UnsupportedParamType;
import io.daff.uacs.core.enums.GrantTypeEnum;
import io.daff.uacs.core.enums.ResponseTypeEnum;
import io.daff.uacs.service.config.shiro.ShiroSessionUtils;
import io.daff.uacs.service.entity.dto.OAuthExtraInfo;
import io.daff.uacs.service.entity.po.AppInfo;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.req.UseProfileRequest;
import io.daff.uacs.service.entity.resp.AuthorizeRequest;
import io.daff.uacs.service.entity.resp.OAuthResponse;
import io.daff.uacs.service.entity.resp.UserProfileResponse;
import io.daff.uacs.service.mapper.AppInfoMapper;
import io.daff.uacs.service.mapper.UserThingsMapper;
import io.daff.uacs.service.service.OAuth2Service;
import io.daff.uacs.service.util.JacksonUtil;
import io.daff.uacs.service.util.JwtUtil;
import io.daff.uacs.service.util.OAuthTokenUtil;
import io.daff.uacs.service.util.SimpleRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author daffupman
 * @since 2020/7/25
 */
@Service
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {

    @Resource
    private UserThingsMapper userThingsMapper;
    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private SimpleRedisUtil simpleRedisUtil;
    @Resource
    private HttpServletRequest request;

    @Override
    public OAuthResponse authorize(AuthorizeRequest authorizeRequest) {

        String responseType = authorizeRequest.getResponseType();
        OAuthResponse oAuthResponse = new OAuthResponse();
        oAuthResponse.setState(authorizeRequest.getState());

        // 校验客户端的合法性
        String appId = authorizeRequest.getAppId();
        AppInfo appInfo = appInfoMapper.selectOne(AppInfo.builder().appId(appId).build());
        if (null == appInfo) {
            throw new NoSuchDataException("客户端不存在");
        }
        String appSecret = authorizeRequest.getAppSecret();
        if (!StringUtils.isEmpty(appSecret) && !appInfo.getAppSecret().equals(appSecret)) {
            throw new InconsistentDataException("客户端id或客户端密钥错误");
        }

        // 校验范围：第三方输入的权限范围是否在自己已经拥有的权限范围之内
        List<String> needScopes = authorizeRequest.getScopes();
        List<String> bindScopes = appInfo.getBindScopes();
        if (!StringUtils.isEmpty(needScopes)) {
            // 如果第三方客户端传来权限范围，才需要校验
            if (!bindScopes.containsAll(needScopes)) {
                // 需要的权限并不都是已经授予的权限
                throw new InsufficientPermissionsException(appId + "权限不足！");
            }
            oAuthResponse.setScope(needScopes);
        }

        // 校验传入的重定向地址是否是该客户端注册时设置的，防止授权码失窃
        String redirectUriFromDb = appInfo.getRedirectUrl();
        if (!redirectUriFromDb.equals(authorizeRequest.getRedirectUri())) {
            throw new ParamValidateException("重定向地址错误");
        }

        if (ResponseTypeEnum.Code.value().equals(responseType)) {
            // 返回授权码（授权码许可）

            // 生成授权码
            String accessToken = authorizeRequest.getAccessToken();
            if (StringUtils.isEmpty(accessToken)) {
                throw new ParamMissException("参数access_token缺失");
            }
            String code = this.generateAuthorizedCode(appId, JwtUtil.getSubjectId(accessToken));
            String needScopesJson = needScopesJson = JacksonUtil.beanToString(needScopes);
            // 授权码和其应该拥有的权限范围绑定
            simpleRedisUtil.set(
                    SystemConstants.AUTHORIZE_CODE_SCOPE_MAP_PREFIX + code,
                    needScopesJson
            );

            oAuthResponse.setCode(code);

        } else if (ResponseTypeEnum.Token.value().equals(responseType)) {
            // 返回token（隐式许可）

            String accessToken = OAuthTokenUtil.generateAccessToken(GrantTypeEnum.IMPLICIT, appId, appInfo.getAppSecret(), appId, appInfo.getAppSecret());
            oAuthResponse.setAccessToken(accessToken);
            oAuthResponse.setTokenType("bearer");
            oAuthResponse.setExpiresIn(OAuthTokenUtil.ACCESS_TOKEN_TTL);

        } else {
            throw new UnsupportedParamType("不支持的响应类型：" + responseType);
        }

        return oAuthResponse;
    }

    @Override
    public OAuthResponse fetchToken(OAuthRequest oAuthRequest) {

        // 验证appId和appSecret
        String appId = oAuthRequest.getAppId();
        AppInfo appInfo = appInfoMapper.selectOne(AppInfo.builder().appId(appId).build());
        if (null == appInfo) {
            throw new NoSuchDataException("客户端不存在:" + appId);
        }
        if (!appInfo.getAppSecret().equals(oAuthRequest.getAppSecret())) {
            throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "appId或appSecret错误");
        }

        OAuthResponse oAuthResponse = new OAuthResponse();
        oAuthResponse.setTokenType("bearer");

        String scope = oAuthRequest.getScope();
        if (!StringUtils.isEmpty(scope)) {
            List<String> bindScopes = appInfo.getBindScopes();
            List<String> needScopes = Arrays.asList(scope.split(":"));
            if (!bindScopes.containsAll(needScopes)) {
                throw new InsufficientPermissionsException(appId + "权限不足！");
            }
            oAuthResponse.setScope(needScopes);
        }

        GrantTypeEnum grantTypeEnum = oAuthRequest.getGrantTypeEnum();
        switch (grantTypeEnum) {
            case AUTHORIZATION_CODE:
                // 授权码许可：grant_type，app_id，app_secret, code，redirect_uri

                // 验证授权码
                String code = oAuthRequest.getCode();
                if (StringUtils.isEmpty(code)) {
                    throw new ParamMissException("授权码参数丢失");
                }
                if (StringUtils.isEmpty(oAuthRequest.getRedirectUri())) {
                    throw new ParamMissException("重定向地址参数丢失");
                }
                String authorizeCodeFormRedis = simpleRedisUtil.get(SystemConstants.AUTHORIZE_CODE_PREFIX + code);
                if (StringUtils.isEmpty(authorizeCodeFormRedis)) {
                    // 授权码会存储在redis中，并设置过期时间，如果查询不到说明已经过期
                    throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "授权码错误");
                }

                // 生成accessToken，refreshToken
                String ssoId = authorizeCodeFormRedis.split(":")[0];
                UserThings userThings = userThingsMapper.selectOne(UserThings.builder().id(Long.valueOf(ssoId)).build());
                doGenerateTokens(grantTypeEnum, ssoId, userThings.getPassword(), appId, appInfo.getAppSecret(), false, oAuthResponse);
                String grantedApp = authorizeCodeFormRedis.split(":")[1];
                if (!Objects.equals(appId, grantedApp)) {
                    log.error("授权码【{}】不是授予此客户端【{}】的", code, appId);
                    throw new BaseException(Hint.BUSINESS_LOGIC_ERROR, "授权码异常");
                }
                String authorizeMapFormRedis = simpleRedisUtil.get(SystemConstants.AUTHORIZE_CODE_SCOPE_MAP_PREFIX + code);
                List<String> scopes = JacksonUtil.stringToBean(authorizeMapFormRedis, new TypeReference<List<String>>() {});
                oAuthResponse.setScope(scopes);

                // 删除授权码、授权码和scope/authorize
                simpleRedisUtil.delete(SystemConstants.AUTHORIZE_CODE_PREFIX + code);
                simpleRedisUtil.delete(SystemConstants.AUTHORIZE_CODE_SCOPE_MAP_PREFIX + code);

                break;
            case PASSWORD:

                // 资源用户者凭据：grant_type，app_id，app_secret, username，password必填，scope选填
                String username = oAuthRequest.getUsername();
                String password = oAuthRequest.getPassword();
                if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                    throw new ParamMissException("用户名或密码参数缺失");
                }
                Subject subject = SecurityUtils.getSubject();
                try {
                    subject.login(new UsernamePasswordToken(username, password));
                } catch (IncorrectCredentialsException e) {
                    log.error("认证登录失败", e);
                    throw new BaseException(Hint.AUTHENTICATION_FAILED, "帐户和密码不一致");
                } catch (AuthenticationException e) {
                    log.error("认证登录失败", e);
                    throw new BaseException(Hint.AUTHENTICATION_FAILED, e.getMessage());
                }

                // 生成accessToken，refreshToken
                UserThings grantedUserThings = (UserThings) subject.getPrincipals().getPrimaryPrincipal();
                doGenerateTokens(grantTypeEnum, String.valueOf(grantedUserThings.getId()), grantedUserThings.getPassword(), appId, appInfo.getAppSecret(), false, oAuthResponse);

                break;
            case CLIENT_CREDENTIALS:

                // 客户端凭证许可：grant_type，app_id，app_secret必填，scope选填
                String appSecret = appInfo.getAppSecret();
                if (!Objects.equals(appSecret, oAuthRequest.getAppSecret())) {
                    throw new InconsistentDataException("app_id或app_secret错误");
                }
                doGenerateTokens(grantTypeEnum, null, null, appId, appInfo.getAppSecret(), false, oAuthResponse);

                break;
            case REFRESH_TOKEN:

                // 刷新令牌：grant_type，app_id，app_secret, refresh_token必填，scope选填

                String refreshToken = oAuthRequest.getRefreshToken();
                if (StringUtils.isEmpty(refreshToken)) {
                    throw new ParamMissException("刷新令牌缺失");
                }

                ssoId = JwtUtil.getSubjectId(refreshToken);
                UserThings userThingsBySsoId = userThingsMapper.selectOne(UserThings.builder().id(Long.valueOf(ssoId)).build());
                try {
                    JwtUtil.verify(refreshToken,ssoId, userThingsBySsoId.getPassword());
                } catch (Exception e) {
                    throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "非法的访问令牌");
                }

                OAuthExtraInfo oAuthExtraInfo = OAuthTokenUtil.getOAuthExtraInfo(refreshToken);
                doGenerateTokens(
                        oAuthExtraInfo.getGrantType(),
                        oAuthExtraInfo.getResourceOwnerId(),
                        oAuthExtraInfo.getResourceOwnerSecret(),
                        oAuthExtraInfo.getAppId(),
                        oAuthExtraInfo.getAppSecret(),
                        true,
                        oAuthResponse
                );

                break;
            default:
                throw new UnsupportedParamType("不支持的授权类型：" + grantTypeEnum.value());
        }

        return oAuthResponse;
    }

//    @Override
//    public ConfirmResponse confirm(ConfirmRequest confirmParam) {
//
//        String appId = confirmParam.getAppId();
//        String appSecret = confirmParam.getAppSecret();
//        AppInfo appInfo = appInfoMapper.selectOne(AppInfo.builder().appId(appId).build());
//        if (appInfo == null) {
//            throw new NoSuchDataException("客户端id不存在");
//        }
//        if (appInfo.getAppSecret().equals(appSecret)) {
//            throw new InconsistentDataException("客户端id或客户端密钥不一致");
//        }
//
//        if (!confirmParam.getConfirm()) {
//            return null;
//        }
//
//        if (StringUtils.isEmpty(confirmParam.getScopes())) {
//            throw new ParamMissException("确认授权时，权限范围必填");
//        }
//
//        String accessToken = request.getHeader(SystemConstants.ACCESS_TOKEN);
//        String userId = JwtUtil.getSubjectId(accessToken);
//
//        return new ConfirmVo(userId, appId);
//    }

    @Override
    public boolean verifyToken(String token) {

        String ssoId = JwtUtil.getSubjectId(token);
        UserThings userThings = userThingsMapper.selectOne(UserThings.builder().id(Long.valueOf(ssoId)).build());
        if (userThings == null || !userThings.getStatus().equals(1)) {
            throw new NoSuchDataException("用户不存在或状态异常");
        }
        if (JwtUtil.isExpired(token)) {
            throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "访问令牌已过期");
        }

        try {
            JwtUtil.verify(token, ssoId, userThings.getPassword());
        } catch (Exception e) {
            throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "访问令牌无效");
        }
        return true;
    }

    @Override
    public UserProfileResponse userProfile(UseProfileRequest useProfileRequest) {
        String accessToken = useProfileRequest.getAccessToken();
        if (JwtUtil.isExpired(accessToken)) {
            throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "过期的访问令牌");
        }

        String subjectId = JwtUtil.getSubjectId(accessToken);
        if (!String.valueOf(useProfileRequest.getUserId()).equals(subjectId)) {
            throw new BaseException(Hint.PARAM_VALIDATE_ERROR, "用户与访问令牌不一致");
        }

        UserThings userThings = userThingsMapper.selectById(useProfileRequest.getUserId());
        return UserProfileResponse.of(userThings);
    }

    /**
     * 生成访问令牌，刷新令牌，并设置到响应数据中
     *
     * @param grantType                  授权类型
     * @param resourceOwnerId            资源拥有者id
     * @param resourceOwnerSecret        资源拥有者密钥
     * @param appId                      客户端id
     * @param appSecret                  客户端密钥
     * @param ignoreGenerateRefreshToken 是否忽略生成刷新令牌，在刷新令牌时候为true，其他一般为false
     * @param oAuthResponse               返回的数据
     */
    private void doGenerateTokens(GrantTypeEnum grantType,
                                  String resourceOwnerId,
                                  String resourceOwnerSecret,
                                  String appId, String appSecret,
                                  boolean ignoreGenerateRefreshToken,
                                  OAuthResponse oAuthResponse) {

        // 查询客户端的权限范围
        AppInfo appInfo = appInfoMapper.selectOne(AppInfo.builder().appId(appId).build());
        List<String> bindScopes = appInfo.getBindScopes();
        oAuthResponse.setScope(bindScopes);

        oAuthResponse.setExpiresIn(OAuthTokenUtil.ACCESS_TOKEN_TTL);
        String accessToken = OAuthTokenUtil.generateAccessToken(grantType, resourceOwnerId, resourceOwnerSecret, appId, appSecret);
        oAuthResponse.setAccessToken(accessToken);

        if (!ignoreGenerateRefreshToken) {
            // 需要刷新令牌
            String refreshToken = OAuthTokenUtil.generateRefreshToken(grantType, resourceOwnerId, resourceOwnerSecret, appId, appSecret);
            oAuthResponse.setRefreshToken(refreshToken);
        }
    }

    /**
     * 生成授权码，需要把当前用户的信息和被授予的客户端信息绑定在一起
     *
     * @param appId      被资源拥有者授予的客户端
     * @param currUserId 被资源拥有者id
     * @return 授权码
     */
    private String generateAuthorizedCode(String appId, String currUserId) {
        // 生成授权码
        String code = OAuthTokenUtil.generateAuthorityCode();

        // 设置有效时间戳(10min内有效)
        // 绑定用户id，appId和有效时间
        simpleRedisUtil.set(
                SystemConstants.AUTHORIZE_CODE_PREFIX + code,
                currUserId + ":" + appId,
                SystemConstants.CODE_EXPIRE_TIME * 60
        );

        return code;
    }

    /**
     * 清除session中的登录信息
     */
    private void clearLoginInfoInSession() {
        ShiroSessionUtils.removeAttribute(SystemConstants.LOGIN_ERROR_RESPONSE);
        ShiroSessionUtils.removeAttribute(SystemConstants.CURRENT_LOGIN_USER);
    }
}

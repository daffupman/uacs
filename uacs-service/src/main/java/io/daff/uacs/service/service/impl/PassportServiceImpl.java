package io.daff.uacs.service.service.impl;

import io.daff.enums.Hint;
import io.daff.exception.BaseException;
import io.daff.exception.ParamMissException;
import io.daff.exception.UnexpectedException;
import io.daff.uacs.core.enums.GrantTypeEnum;
import io.daff.uacs.service.config.shiro.token.MobilePhoneCodeToken;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.resp.OAuthResponse;
import io.daff.uacs.service.service.BaseService;
import io.daff.uacs.service.service.PassportService;
import io.daff.uacs.service.util.OAuthTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


/**
 * @author daff
 * @since 2021/5/21
 */
@Service
@Slf4j
public class PassportServiceImpl implements PassportService {

    @Resource
    private BaseService baseService;

    /**
     * 登录类型：帐密
     */
    private static final String TYPE_AP = "ap";

    /**
     * 登录类型：手机号验证码
     */
    private static final String TYPE_MC = "mc";
    private String appId = "a653";
    private String appSecret = "a653";

    @Override
    public OAuthResponse signIn(OAuthRequest oAuthRequest) {

        AuthenticationToken token;
        Subject subject = SecurityUtils.getSubject();
        String account = oAuthRequest.getAccount();

        // 根据不同的登录方式生成不同token
        if (TYPE_AP.equals(oAuthRequest.getType())) {
            // 帐密登录
            String password = oAuthRequest.getPassword();
            if (StringUtils.isEmpty(password)) {
                throw new ParamMissException("密码必填");
            }

            // 使用用户名密码Token
            token = new UsernamePasswordToken(account, password);
        } else if (TYPE_MC.equals(oAuthRequest.getType())) {
            // 手机号验证码登录

            String code = oAuthRequest.getCode();
            if (StringUtils.isEmpty(code)) {
                throw new ParamMissException("验证码必填");
            }
            // 生成手机号Token
            token = new MobilePhoneCodeToken(account, code);
        } else {
            log.error("登录类型非法，登录帐户：{}", oAuthRequest.getAccount());
            throw new UnexpectedException("登录类型非法");
        }

        // 使用相应的token登录
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            log.error("认证登录失败", e);
            throw new BaseException(Hint.AUTHENTICATION_FAILED, "帐户和密码不一致");
        } catch (ExcessiveAttemptsException | UnknownAccountException e) {
            log.error("认证登录失败", e);
            throw new BaseException(Hint.AUTHENTICATION_FAILED, e.getMessage());
        } catch (AuthenticationException e) {
            log.error("认证登录失败", e);
            throw new BaseException(Hint.AUTHENTICATION_FAILED, "登录失败，请联系官方");
        }

        // TODO 记录用户登录到mongo

        UserThings userThings = baseService.matchAccount(account);
        String accessToken = OAuthTokenUtil.generateAccessToken(GrantTypeEnum.PASSWORD, userThings.getId().toString(), userThings.getPassword(), appId, appSecret);
        String refreshToken = OAuthTokenUtil.generateRefreshToken(GrantTypeEnum.PASSWORD, userThings.getId().toString(), userThings.getPassword(), appId, appSecret);
        return new OAuthResponse(userThings.getId(), userThings.getName(), userThings.getNickName(), accessToken, refreshToken);
    }
}

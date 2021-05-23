package io.daff.uacs.service.util;

import io.daff.uacs.core.enums.GrantTypeEnum;
import io.daff.uacs.core.enums.TokenTypeEnum;
import io.daff.uacs.service.entity.dto.OAuthExtraInfo;

import java.util.UUID;

/**
 * OAuth2.0授权协议中的用于生成token的工具类
 *
 * @author daffupman
 * @since 2020/7/27
 */
public class OAuthTokenUtil {

    /**
     * 访问令牌的存活时间（min）
     */
    public static final Long ACCESS_TOKEN_TTL = 30L;

    /**
     * 刷新令牌的存活时间（min）
     */
    public static final Long REFRESH_TOKEN_TTL = 30L * 24 * 60;

    /**
     * 生成accessToken
     *
     * @param grantType           使用OAuth2.0的授权模式
     * @param resourceOwnerId     资源拥有者的id
     * @param resourceOwnerSecret 资源拥有者的密钥
     * @param appId               客户端id
     * @param appSecret           客户端
     * @return 返回accessToken
     */
    public static String generateAccessToken(GrantTypeEnum grantType,
                                             String resourceOwnerId,
                                             String resourceOwnerSecret,
                                             String appId,
                                             String appSecret) {

        return generateToken(grantType, TokenTypeEnum.ACCESS_TOKEN, resourceOwnerId, resourceOwnerSecret, appId, appSecret, ACCESS_TOKEN_TTL);
    }

    /**
     * 生成refreshToken
     *
     * @param grantType           使用OAuth2.0的授权模式
     * @param resourceOwnerId     资源拥有者的id
     * @param resourceOwnerSecret 资源拥有者的密钥
     * @param appId               客户端id
     * @param appSecret           客户端
     * @return 返回accessToken
     */
    public static String generateRefreshToken(GrantTypeEnum grantType,
                                              String resourceOwnerId,
                                              String resourceOwnerSecret,
                                              String appId,
                                              String appSecret) {

        return generateToken(grantType, TokenTypeEnum.REFRESH_TOKEN, resourceOwnerId, resourceOwnerSecret, appId, appSecret, REFRESH_TOKEN_TTL);
    }

    /**
     * 生成token
     *
     * @param grantType           使用OAuth2.0的授权模式
     * @param tokenType           令牌类型
     * @param resourceOwnerId     资源拥有者的id
     * @param resourceOwnerSecret 资源拥有者的密钥
     * @param appId               客户端id
     * @param appSecret           客户端
     * @return 返回token
     */
    public static String generateToken(GrantTypeEnum grantType,
                                       TokenTypeEnum tokenType,
                                       String resourceOwnerId,
                                       String resourceOwnerSecret,
                                       String appId,
                                       String appSecret,
                                       Long ttl) {

        // // 优先使用resourceOwnerId,没有的话使用appId
        // String subjectId = !StringUtils.isEmpty(resourceOwnerId) ? resourceOwnerId : appId;
        // // 优先使用resourceOwnerSecret,没有的话使用appSecret
        // String secret = !StringUtils.isEmpty(resourceOwnerSecret) ? resourceOwnerSecret : appSecret;

        //OAuth相关数据
        OAuthExtraInfo oAuthExtraInfo = new OAuthExtraInfo();
        oAuthExtraInfo.setGrantType(grantType);
        oAuthExtraInfo.setResourceOwnerId(resourceOwnerId);
        oAuthExtraInfo.setResourceOwnerSecret(resourceOwnerSecret);
        oAuthExtraInfo.setAppId(appId);
        oAuthExtraInfo.setAppSecret(appSecret);
        oAuthExtraInfo.setTtl(ttl);

        return JwtUtil.create(resourceOwnerId, resourceOwnerSecret, oAuthExtraInfo, tokenType);
    }

    public static String generateAuthorityCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

    public static OAuthExtraInfo getOAuthExtraInfo(String token) {
        return JwtUtil.getOAuthExtraInfo(token);
    }
}

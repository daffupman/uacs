package io.daff.uacs.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.daff.uacs.core.enums.GrantTypeEnum;
import io.daff.uacs.core.enums.TokenTypeEnum;
import io.daff.uacs.service.entity.dto.OAuthExtraInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * jwt工具类
 *
 * @author daffupman
 * @since 2020/7/12
 */
@Slf4j
public class JwtUtil {

    /**
     * 令牌颁发者，表示该令牌由谁来创建的，很多OAuth部署中会将设为授权服务器的URL
     */
    private static final String ISSUER = "io.daff";

    /**
     * jwt claim常量名
     */
    private static final String APP_ID = "appId";
    private static final String APP_SECRET = "appSecret";
    private static final String GRANT_TYPE = "grantType";
    private static final String TOKEN_TYPE = "tokenType";
    private static final String RESOURCE_OWNER_ID = "resourceOwnerId";
    private static final String RESOURCE_OWNER_SECRET = "resourceOwnerSecret";
    private static final String EXPIRE_DATE = "expireDate";

    /**
     * 加密必要信息生成jwt token
     *
     * @param subjectId 用户的唯一标识
     * @param secret    用户的密钥
     * @param tokenType
     * @return 返回jwt token
     */
    public static String create(String subjectId, String secret, OAuthExtraInfo oAuthExtraInfo, TokenTypeEnum tokenType) {
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
        Date expireDate = new Date(currentTimeMillis + oAuthExtraInfo.getTtl() * 1000 * 60);
        oAuthExtraInfo.setExpireDate(expireDate);
        try {
            return JWT.create()
                    // 令牌的唯一标志符。该声明的值在令牌颁发者创建的每一个令牌中都是唯一的，
                    // 为了防止冲突，它通常是一个密码学随机值。这个值相当于向结构化令牌中加入了一个攻击无法获得的随机熵组件，
                    // 有利于防止令牌猜测攻击和重放攻击。
                    .withJWTId(UUID.randomUUID().toString().replace("-", ""))
                    // 令牌的主题。它表示该令牌是关于谁的，在很多OAuth部署中会将它设为资源拥有者的唯一标识。在大多数情况下，
                    // 主体在同一个颁发者的范围内必须是唯一的。
                    .withSubject(subjectId)
                    // 令牌颁发者，表示该令牌由谁来创建的，很多OAuth部署中会将设为授权服务器的URL
                    .withIssuer(ISSUER)
                    // 令牌接收者
                    .withAudience(oAuthExtraInfo.getAppId())
                    // 令牌颁布时间
                    .withIssuedAt(now)
                    // 签名过期时间
                    .withExpiresAt(expireDate)
                    // 业务数据
                    .withClaim(GRANT_TYPE, oAuthExtraInfo.getGrantType().value())
                    .withClaim(TOKEN_TYPE, tokenType.getValue())
                    .withClaim(APP_ID, oAuthExtraInfo.getAppId())
                    .withClaim(APP_SECRET, oAuthExtraInfo.getAppSecret())
                    .withClaim(RESOURCE_OWNER_ID, oAuthExtraInfo.getResourceOwnerId())
                    .withClaim(RESOURCE_OWNER_SECRET, oAuthExtraInfo.getResourceOwnerSecret())
                    .withClaim(EXPIRE_DATE, expireDate)
                    // 签名密钥
                    .sign(Algorithm.HMAC256(secret));
        } catch (UnsupportedEncodingException e) {
            log.error("生成jwt token失败", e);
            return null;
        }
    }

    /**
     * 验证jwt token
     *
     * @param token     jwt token
     * @param subjectId 用户id
     * @param secret    用户密钥
     */
    public static void verify(String token, String subjectId, String secret) throws Exception {

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subjectId)
                .build();
        verifier.verify(token);
    }

    /**
     * 从token中解析出OAuth相关数据
     *
     * @param token jwt token
     * @return token中的OAuth相关数据
     */
    public static OAuthExtraInfo getOAuthExtraInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            OAuthExtraInfo oAuthExtraInfo = new OAuthExtraInfo();
            for (GrantTypeEnum value : GrantTypeEnum.values()) {
                if (jwt.getClaim(GRANT_TYPE).asString().equals(value.value())) {
                    oAuthExtraInfo.setGrantType(value);
                }
            }
            oAuthExtraInfo.setAppId(jwt.getClaim(APP_ID).asString());
            oAuthExtraInfo.setAppSecret(jwt.getClaim(APP_SECRET).asString());
            oAuthExtraInfo.setResourceOwnerId(jwt.getClaim(RESOURCE_OWNER_ID).asString());
            oAuthExtraInfo.setResourceOwnerSecret(jwt.getClaim(RESOURCE_OWNER_SECRET).asString());
            oAuthExtraInfo.setTokenType(jwt.getClaim(TOKEN_TYPE).asString());
            oAuthExtraInfo.setExpireDate(jwt.getClaim(EXPIRE_DATE).asDate());

            return oAuthExtraInfo;
        } catch (JWTDecodeException e) {
            log.error("token解析失败", e);
            return null;
        }
    }

    /**
     * 判断token是否过期
     *
     * @param token jwt token
     * @return 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (JWTDecodeException e) {
            log.error("token已经过期");
            return false;
        }
    }

    /**
     * 从token获取subjectId
     * @param jwtToken token
     * @return token中的subjectId
     */
    public static String getSubjectId(String jwtToken) {
        try {
            DecodedJWT jwt = JWT.decode(jwtToken);
            return jwt.getSubject();
        } catch (JWTDecodeException e) {
            log.error("token解析失败", e);
            return null;
        }
    }

}

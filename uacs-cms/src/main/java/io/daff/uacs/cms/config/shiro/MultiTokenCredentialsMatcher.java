package io.daff.uacs.cms.config.shiro;

import io.daff.uacs.cms.config.shiro.token.JwtToken;
import io.daff.uacs.cms.config.shiro.token.MobilePhoneCodeToken;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.util.JwtUtil;
import io.daff.uacs.service.util.SimpleRedisUtil;
import io.daff.utils.crypto.StrongCryptoUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 凭证验证
 *
 * @author daff
 * @since 2020/8/9
 */
public class MultiTokenCredentialsMatcher implements CredentialsMatcher {

    private final SimpleRedisUtil simpleRedisUtil;
    private static final int RETRIES_LIMIT = 3;

    public MultiTokenCredentialsMatcher(SimpleRedisUtil simpleRedisUtil) {
        this.simpleRedisUtil = simpleRedisUtil;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        if (!(info instanceof SimpleAuthenticationInfo)) {
            return false;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = (SimpleAuthenticationInfo) info;

        if (token instanceof UsernamePasswordToken) {
            // 帐密token
            UsernamePasswordToken upToken = (UsernamePasswordToken) token;
            // 明文密码
            String plainPassword = new String(upToken.getPassword());
            // 密文密码（数据库中的密码）
            String hashedPassword = simpleAuthenticationInfo.getCredentials().toString();
            String salt = new String(simpleAuthenticationInfo.getCredentialsSalt().getBytes());

            String retries = simpleRedisUtil.get("retries:" + upToken.getUsername());
            int failRetries = StringUtils.isEmpty(retries) ? 0 : Integer.parseInt(retries);
            if (failRetries >= RETRIES_LIMIT) {
                throw new ExcessiveAttemptsException("密码输入错误超过" + RETRIES_LIMIT + "次，请30分钟后重试");
            }

            boolean success = StrongCryptoUtil.validate(plainPassword, hashedPassword, salt);
            synchronized (MultiTokenCredentialsMatcher.class) {
                if (!success) {
                    simpleRedisUtil.set("retries:" + upToken.getUsername(), String.valueOf(failRetries + 1), 60 * 30);
                }
            }
            return success;
        }

        if (token instanceof MobilePhoneCodeToken) {
            // 手机号验证码token
            MobilePhoneCodeToken mcToken = (MobilePhoneCodeToken) token;
            String mobilePhone = mcToken.getPrincipal().toString();
            String code = mcToken.getCredentials().toString();
            // 从redis中获取
            String codeFromRedis = simpleRedisUtil.get("code:" + mobilePhone);
            return Objects.equals(codeFromRedis, code);
        }

        if (token instanceof JwtToken) {
            // jwt token
            JwtToken jwtToken = (JwtToken) token;
            String accessToken = jwtToken.getPrincipal();
            if (JwtUtil.isExpired(accessToken)) {
                return false;
            }
            UserThings userThings = (UserThings) simpleAuthenticationInfo.getPrincipals().getPrimaryPrincipal();
            try {
                JwtUtil.verify(accessToken, String.valueOf(userThings.getId()), userThings.getPassword());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}

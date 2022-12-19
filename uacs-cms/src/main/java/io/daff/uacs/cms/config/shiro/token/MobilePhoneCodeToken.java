package io.daff.uacs.cms.config.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 手机号验证码 token凭证
 *
 * @author daff
 * @since 2020/7/12
 */
public class MobilePhoneCodeToken implements AuthenticationToken{

    private static final long serialVersionUID = 5292976771069728336L;
    private final String mobilePhoneNum;
    private final String code;

    public MobilePhoneCodeToken(String mobilePhoneNum, String code) {
        this.mobilePhoneNum = mobilePhoneNum;
        this.code = code;
    }

    @Override
    public Object getPrincipal() {
        return mobilePhoneNum;
    }

    @Override
    public Object getCredentials() {
        return code;
    }
}

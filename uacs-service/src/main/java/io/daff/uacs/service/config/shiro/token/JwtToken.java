package io.daff.uacs.service.config.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * jwt token凭证
 *
 * @author daff
 * @since 2020/7/12
 */
public class JwtToken implements AuthenticationToken{

    private static final long serialVersionUID = 5292976771069728336L;
    private String jwtToken;

    public JwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public String getPrincipal() {
        return jwtToken;
    }

    @Override
    public String getCredentials() {
        return jwtToken;
    }
}

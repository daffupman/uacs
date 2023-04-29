package io.daff.uacs.web.interceptor.sign;

/**
 * @author daff
 * @since 2023/1/6
 */
public interface ApiAccessAuthenticator {

    void auth(Signature signature);
}

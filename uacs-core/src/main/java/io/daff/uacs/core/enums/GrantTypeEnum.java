package io.daff.uacs.core.enums;

/**
 * OAuth2的授权模式
 *
 * @author daffupman
 * @since 2020/7/25
 */
public enum GrantTypeEnum {

    /**
     * 授权码许可
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * 资源拥有者凭据许可
     */
    PASSWORD("password"),

    /**
     * 客户端凭据许可
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * 隐式许可
     */
    IMPLICIT("implicit"),

    /**
     * 非OAuth2.0的授权模式，只是入参的一种可能性，故放在此处
     */
    REFRESH_TOKEN("refresh_token"),
    ;

    private String value;

    GrantTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

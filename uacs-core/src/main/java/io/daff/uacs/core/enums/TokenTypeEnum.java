package io.daff.uacs.core.enums;

/**
 * 令牌类型
 *
 * @author daffupman
 * @since 2020/10/3
 */
public enum TokenTypeEnum {

    /**
     * 访问令牌
     */
    ACCESS_TOKEN("access_token"),

    /**
     * 刷新令牌
     */
    REFRESH_TOKEN("refresh_token"),
    ;
    private String value;

    TokenTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

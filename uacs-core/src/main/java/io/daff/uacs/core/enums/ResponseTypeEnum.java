package io.daff.uacs.core.enums;

/**
 * OAuth2授权接口的响应类型枚举
 *
 * @author daffupman
 * @since 2020/7/25
 */
public enum ResponseTypeEnum {

    /**
     * 返回授权码
     */
    Code("code"),

    /**
     * 返回访问令牌
     */
    Token("token"),
    ;

    private String value;

    ResponseTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

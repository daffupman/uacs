package io.daff.uacs.core.enums;

import java.util.Optional;

/**
 * 权限范围
 *
 * @author daffupman
 * @since 2020/8/15
 */
public enum ScopeEnum {

    CREATE(1, "create", "创建"),
    READ(2, "read", "查询"),
    UPDATE(4, "update", "修改"),
    DELETE(8, "delete", "删除"),

    ;
    private final Integer code;
    private final String name;
    private final String desc;

    ScopeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 获取code对应的Scope
     */
    public static Optional<ScopeEnum> convertToScope(int code) {
        for (ScopeEnum scope : ScopeEnum.values()) {
            if (scope.code == code) {
                return Optional.of(scope);
            }
        }
        return Optional.empty();
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}

package io.daff.uacs.core.common;

/**
 * @author daffupman
 * @since 2020/10/7
 */
public class Permissions {

    /**
     * 用户
     */
    public static final String USER_LIST = "user:list";
    public static final String USER_ADD = "user:add";
    public static final String USER_EDIT = "user:edit";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_ASSIGN_ROLE = "user:assign-role";
    public static final String USER_NOT_ASSIGN_ROLE = "user:not-assign-role";

    /**
     * 角色
     */
    public static final String ROLE_LIST = "role:list";
    public static final String ROLE_ADD = "role:add";
    public static final String ROLE_EDIT = "role:edit";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN_PERMISSION = "role:assign-permission";
    public static final String ROLE_NOT_ASSIGN_PERMISSION = "role:not-assign-permission";

    /**
     * 权限
     */
    public static final String PERMISSION_LIST = "permission:list";
    public static final String PERMISSION_ADD = "permission:add";
    public static final String PERMISSION_EDIT = "permission:edit";
    public static final String PERMISSION_DELETE = "permission:delete";
}

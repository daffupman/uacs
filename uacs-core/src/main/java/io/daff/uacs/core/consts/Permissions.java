package io.daff.uacs.core.consts;

/**
 * @author daffupman
 * @since 2020/10/7
 */
public interface Permissions {

    /**
     * 用户
     */
    String USER_LIST = "user:list";
    String USER_ADD = "user:add";
    String USER_EDIT = "user:edit";
    String USER_DELETE = "user:delete";
    String USER_ASSIGN_ROLE = "user:assign-role";
    String USER_NOT_ASSIGN_ROLE = "user:not-assign-role";

    /**
     * 角色
     */
    String ROLE_LIST = "role:list";
    String ROLE_ADD = "role:add";
    String ROLE_EDIT = "role:edit";
    String ROLE_DELETE = "role:delete";
    String ROLE_ASSIGN_PERMISSION = "role:assign-permission";
    String ROLE_NOT_ASSIGN_PERMISSION = "role:not-assign-permission";

    /**
     * 权限
     */
    String PERMISSION_LIST = "permission:list";
    String PERMISSION_ADD = "permission:add";
    String PERMISSION_EDIT = "permission:edit";
    String PERMISSION_DELETE = "permission:delete";
}

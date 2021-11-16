package io.daff.uacs.cms.service;

import io.daff.uacs.service.entity.po.Permission;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.entity.po.UserThings;

import java.util.List;
import java.util.Set;

/**
 * @author daffupman
 * @since 2020/10/3
 */
public interface BaseService {

    /**
     * 查询用户
     */
    UserThings getUserByCondition(UserThings userThings);

    /**
     * 查询用户拥有的角色
     * @param userId
     */
    List<Role> getUserRolesBySsoId(Long userId);

    /**
     * 查询用户拥有的权限
     * @param userId
     */
    List<Permission> getUserPermissionsBySsoId(Long userId);

    /**
     * 匹配用户输入的账号，登录名不可以是纯数字，也不能包含@符号
     */
    UserThings matchAccount(String account);

    /**
     * 查询用户可见的层级
     */
    Set<String> getHierarchyList(String curUserSsoId);

    /**
     * 获取用户身份
     */
    String getIdentity(Long curUserSsoId);

    /**
     * 用户是否可见指定层级
     */
    boolean userVisibleHierarchyList(String curUserSsoId, List<String> hierarchyIdList);

    boolean userVisibleRoleList(Long currUserSsoId, List<String> roleIds);
}

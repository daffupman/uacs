package io.daff.uacs.cms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.core.consts.Roles;
import io.daff.uacs.service.entity.po.AppInfo;
import io.daff.uacs.service.entity.po.Permission;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.mapper.AppInfoMapper;
import io.daff.uacs.service.mapper.HierarchyMapper;
import io.daff.uacs.service.mapper.PermissionMapper;
import io.daff.uacs.service.mapper.RoleMapper;
import io.daff.uacs.service.mapper.UserThingsMapper;
import io.daff.uacs.service.util.JacksonUtil;
import io.daff.uacs.service.util.SimpleRedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2021/5/21
 */
@Service
public class BaseServiceImpl implements BaseService {

    @Resource
    private UserThingsMapper userThingsMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private HierarchyMapper hierarchyMapper;
    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private SimpleRedisUtil simpleRedisUtil;

    @Override
    public UserThings getUserByCondition(UserThings userThings) {
        return userThingsMapper.selectOne(userThings);
    }

    @Override
    public List<Role> getUserRolesBySsoId(Long userId) {

        List<Role> roleList;

        String key = "roles:" + userId;
        String cachedRoles = simpleRedisUtil.get(key);
        if (!StringUtils.isEmpty(cachedRoles)) {
            roleList = JacksonUtil.stringToBean(cachedRoles, new TypeReference<List<Role>>() {
            });
        } else {
            roleList = roleMapper.selectByUserId(userId);
            simpleRedisUtil.set(key, JacksonUtil.beanToString(roleList), 60);
        }

        return roleList;
    }

    @Override
    public List<AppInfo> getUserAppInfosBySsoId(Long userId) {
        List<AppInfo> appInfoList;

        String key = "app_infos:" + userId;
        String cachedAppInfos = simpleRedisUtil.get(key);
        if (!StringUtils.isEmpty(cachedAppInfos)) {
            appInfoList = JacksonUtil.stringToBean(cachedAppInfos, new TypeReference<List<AppInfo>>() {
            });
        } else {
            appInfoList = appInfoMapper.selectByUserId(userId);
            simpleRedisUtil.set(key, JacksonUtil.beanToString(appInfoList), 60);
        }

        return appInfoList;
    }

    @Override
    public List<Permission> getUserPermissionsBySsoId(Long ssoId) {

        String key = "permissions:" + ssoId;
        String userPermissions = simpleRedisUtil.get(key);
        if (!StringUtils.isEmpty(userPermissions)) {
            return JacksonUtil.stringToBean(userPermissions, new TypeReference<List<Permission>>() {
            });
        }

        List<Role> RoleList = getUserRolesBySsoId(ssoId);
        if (CollectionUtils.isEmpty(RoleList)) {
            return new ArrayList<>(0);
        }
        List<Permission> permissions;
        boolean isSuperAdmin = RoleList.stream().anyMatch(each -> Roles.SUPER_ADMIN.equalsIgnoreCase(each.getName()));
        if (isSuperAdmin) {
            permissions = permissionMapper.selectAll()
                    .stream().map(each -> Permission.builder().id(each.getId())
                            .code(each.getCode()).build()
                    ).collect(Collectors.toList());
        } else {
            permissions = permissionMapper.selectByRoleIds(
                    RoleList.stream().map(Role::getId).collect(Collectors.toList())
            );
        }
        simpleRedisUtil.set(key, JacksonUtil.beanToString(permissions));
        return permissions;
    }

    public UserThings matchAccount(String account) {

        UserThings userThings;
        if (account.contains("@")) {
            userThings = UserThings.builder().email(account).build();
        } else if (Pattern.compile("^1[3-9]\\d{9}$").matcher(account).matches()) {
            userThings = UserThings.builder().mobilePhoneNo(account).build();
        } else {
            userThings = UserThings.builder().name(account).build();
        }

        if (userThings != null) {
            userThings = getUserByCondition(userThings);
        }

        return userThings;
    }

    @Override
    public Set<String> getHierarchyList(String curUserSsoId) {

        List<Integer> limitHierarchyList = null;
        if (!Roles.SUPER_ADMIN.equals(getIdentity(Long.parseLong(curUserSsoId)))) {
            UserThings userThingsById = userThingsMapper.selectOne(UserThings.builder().id(Long.parseLong(curUserSsoId)).build());
            limitHierarchyList = hierarchyMapper.selectHierarchyListByParentId(userThingsById.getTopHierarchy());
        }
        return new HashSet<>(hierarchyMapper.listByIdentity(limitHierarchyList));
    }

    @Override
    public String getIdentity(Long curUserSsoId) {
        List<String> roleNameList = getUserRolesBySsoId(curUserSsoId).stream().map(Role::getName).collect(Collectors.toList());
        return roleNameList.contains(Roles.SUPER_ADMIN) ? Roles.SUPER_ADMIN : Roles.COMMON_USER;
    }

    @Override
    public boolean userVisibleHierarchyList(String curUserSsoId, List<String> hierarchyIdList) {
        return getHierarchyList(curUserSsoId).containsAll(hierarchyIdList);
    }

    @Override
    public boolean userVisibleRoleList(Long currUserSsoId, List<String> roleIds) {
        if (getIdentity(currUserSsoId).equalsIgnoreCase(Roles.SUPER_ADMIN)) {
            return true;
        }
        return getUserRolesBySsoId(currUserSsoId).stream().map(Role::getId).collect(Collectors.toList()).containsAll(roleIds);
    }

    @Override
    public boolean userVisibleAppInfoList(Long currUserSsoId, List<Integer> appInfoIds) {
        if (getIdentity(currUserSsoId).equalsIgnoreCase(Roles.SUPER_ADMIN)) {
            return true;
        }
        return getUserAppInfosBySsoId(currUserSsoId).stream().map(AppInfo::getId).collect(Collectors.toList()).containsAll(appInfoIds);
    }
}

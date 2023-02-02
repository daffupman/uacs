package io.daff.uacs.cms.service.impl;

import com.github.pagehelper.PageInfo;
import io.daff.uacs.cms.entity.req.RoleRequest;
import io.daff.uacs.cms.entity.req.RoleSortableQueryRequest;
import io.daff.uacs.cms.entity.resp.RoleResponse;
import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.cms.service.RoleService;
import io.daff.uacs.cms.util.PageUtil;
import io.daff.uacs.core.consts.Roles;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.uacs.service.mapper.RoleMapper;
import io.daff.utils.common.StringUtil;
import io.daff.web.consts.GlobalConstants;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2021/5/17
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private BaseService baseService;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private HttpServletRequest request;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveOrUpdateRole(RoleRequest roleRequest) {

        Long currUserId = Long.valueOf((String) request.getAttribute(GlobalConstants.CURRENT_LOGIN_USER));

        int effectRows;

        Role putRole = Role.builder().id(roleRequest.getId())
                .name(roleRequest.getName())
                .desc(roleRequest.getDesc())
                .updateBy(currUserId).build();

        if (!StringUtils.isEmpty(roleRequest.getId())) {
            // 修改角色
            if (!baseService.userVisibleRoleList(currUserId, Collections.singletonList(roleRequest.getId()))) {
                throw new BaseException(Hint.AUTHORITY_FAILED, "用户权限不足");
            }
            putRole.setUpdateBy(currUserId);
            effectRows = roleMapper.update(putRole);
        } else {
            // 新增角色
            putRole.setId(StringUtil.uuid());
            putRole.setCreateBy(currUserId);
            effectRows = roleMapper.insert(putRole);
        }
        return effectRows > 0 ? putRole.getId() : null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeRole(String roleId) {

        Long currUserId = Long.valueOf((String) request.getAttribute(GlobalConstants.CURRENT_LOGIN_USER));
        if (!baseService.userVisibleRoleList(currUserId, Collections.singletonList(roleId))) {
            throw new BaseException(Hint.AUTHORITY_FAILED, "用户权限不足");
        }

        Role role = roleMapper.selectById(roleId);
        if (role != null) {
            return false;
        }

        int effectRows = roleMapper.deleteById(roleId);
        return effectRows > 0;
    }

    @Override
    public Page<RoleResponse> pagingQueryRoles(RoleSortableQueryRequest roleSortableQueryRequest) {

        Long currUserId = Long.valueOf((String) request.getAttribute(GlobalConstants.CURRENT_LOGIN_USER));

        List<String> currUserRoleIds = null;
        if (!baseService.getIdentity(currUserId).equalsIgnoreCase(Roles.SUPER_ADMIN)) {
            currUserRoleIds = baseService.getUserRolesBySsoId(currUserId).stream().map(Role::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(currUserRoleIds)) {
                return new Page<>();
            }
        }

        PageUtil.startPage(roleSortableQueryRequest, RoleResponse.class);
        List<Role> roles = roleMapper.selectByConditions(roleSortableQueryRequest.getName(), currUserRoleIds);
        PageInfo<Role> rolePageInfo = new PageInfo<>(roles);
        List<RoleResponse> roleResponses = roles.stream().map(RoleResponse::of).collect(Collectors.toList());
        return Page.of(rolePageInfo.getTotal(), roleResponses);
    }

    @Override
    public RoleResponse roleDetail(String roleId) {
        Long currUserId = Long.valueOf((String) request.getAttribute(GlobalConstants.CURRENT_LOGIN_USER));
        if (!baseService.userVisibleRoleList(currUserId, Collections.singletonList(roleId))) {
            throw new BaseException(Hint.AUTHORITY_FAILED, "用户权限不足");
        }
        Role role = roleMapper.selectById(roleId);
        return RoleResponse.of(role);
    }
}

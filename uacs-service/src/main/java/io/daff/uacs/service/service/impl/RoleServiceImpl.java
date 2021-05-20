package io.daff.uacs.service.service.impl;

import com.github.pagehelper.PageInfo;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.entity.req.RoleQueryRequest;
import io.daff.uacs.service.entity.req.RoleRequest;
import io.daff.uacs.service.entity.resp.RoleResponse;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.uacs.service.mapper.RoleMapper;
import io.daff.uacs.service.service.RoleService;
import io.daff.uacs.service.util.PageUtil;
import io.daff.util.CopyUtil;
import io.daff.util.StringHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
    private RoleMapper roleMapper;

    @Override
    public String saveOrUpdateRole(RoleRequest roleRequest) {

        // TODO：登录功能完成后补充
        Long currUserId = 100000000L;

        int effectRows;

        Role putRole = Role.builder().id(roleRequest.getId())
                .name(roleRequest.getName())
                .desc(roleRequest.getDesc())
                .updateBy(currUserId).build();

        if (!StringUtils.isEmpty(roleRequest.getId())) {
            // 修改角色
            putRole.setUpdateBy(currUserId);
            effectRows = roleMapper.batchUpdate(Collections.singletonList(putRole));
        } else {
            // 新增角色
            putRole.setId(StringHelper.uuid());
            putRole.setCreateBy(currUserId);
            effectRows = roleMapper.batchInsert(Collections.singletonList(putRole));
        }
        return effectRows > 0 ? putRole.getId() : null;
    }

    @Override
    public Boolean removeRole(String roleId) {

        List<Role> roles = roleMapper.selectByIds(Collections.singletonList(roleId));
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        int effectRows = roleMapper.deleteByIds(Collections.singletonList(roleId));
        return effectRows > 0;
    }

    @Override
    public Page<RoleResponse> pagingQueryRoles(RoleQueryRequest roleQueryRequest) {
        PageUtil.startPage(roleQueryRequest, RoleResponse.class);
        List<Role> roles = roleMapper.select(
                Role.builder().name(roleQueryRequest.getName()).build()
        );
        PageInfo<Role> rolePageInfo = new PageInfo<>(roles);
        List<RoleResponse> roleResponses = roles.stream().map(RoleResponse::of).collect(Collectors.toList());
        return Page.of(rolePageInfo.getTotal(), roleResponses);
    }
}

package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.RoleQueryRequest;
import io.daff.uacs.service.entity.req.RoleRequest;
import io.daff.uacs.service.entity.resp.RoleResponse;
import io.daff.uacs.service.entity.resp.base.Page;

/**
 * @author daff
 * @since 2021/5/17
 */
public interface RoleService {

    String saveOrUpdateRole(RoleRequest roleRequest);

    Boolean removeRole(String roleId);

    Page<RoleResponse> pagingQueryRoles(RoleQueryRequest roleQueryRequest);
}

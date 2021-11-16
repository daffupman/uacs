package io.daff.uacs.cms.service;

import io.daff.uacs.cms.entity.req.RoleRequest;
import io.daff.uacs.cms.entity.req.RoleSortableQueryRequest;
import io.daff.uacs.cms.entity.resp.RoleResponse;
import io.daff.uacs.service.entity.resp.base.Page;

/**
 * @author daff
 * @since 2021/5/17
 */
public interface RoleService {

    String saveOrUpdateRole(RoleRequest roleRequest);

    Boolean removeRole(String roleId);

    Page<RoleResponse> pagingQueryRoles(RoleSortableQueryRequest roleSortableQueryRequest);

    RoleResponse roleDetail(String roleId);
}

package io.daff.uacs.cms.controller;

import io.daff.uacs.cms.entity.req.RoleRequest;
import io.daff.uacs.cms.entity.req.RoleSortableQueryRequest;
import io.daff.uacs.cms.service.RoleService;
import io.daff.uacs.cms.entity.resp.RoleResponse;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.web.entity.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author daff
 * @since 2021/5/17
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PutMapping
    public Response<String> modifyRole(@RequestBody @Validated RoleRequest roleRequest) {
        String roleId = roleService.saveOrUpdateRole(roleRequest);
        return Response.ok(roleId);
    }

    @GetMapping
    public Response<Page<RoleResponse>> pagingQueryRoles(@NotNull(message = "请求参数为空")
                                                         @Validated RoleSortableQueryRequest roleSortableQueryRequest) {

        Page<RoleResponse> roleResponsePage = roleService.pagingQueryRoles(roleSortableQueryRequest);
        return Response.ok(roleResponsePage);
    }

    @DeleteMapping("/{roleId}")
    public Response<Void> removeRoles(@PathVariable("roleId") String roleId) {
        roleService.removeRole(roleId);
        return Response.ok();
    }

    @GetMapping("/{roleId}")
    public Response<RoleResponse> roleDetail(@PathVariable("roleId") String roleId) {
        RoleResponse roleResponse = roleService.roleDetail(roleId);
        return Response.ok(roleResponse);
    }
}

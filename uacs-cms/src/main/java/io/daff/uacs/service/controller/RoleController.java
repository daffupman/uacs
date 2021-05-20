package io.daff.uacs.service.controller;

import io.daff.entity.Response;
import io.daff.uacs.service.entity.req.RoleQueryRequest;
import io.daff.uacs.service.entity.req.RoleRequest;
import io.daff.uacs.service.entity.resp.RoleResponse;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.uacs.service.service.RoleService;
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
                                                         @Validated RoleQueryRequest roleQueryRequest) {

        Page<RoleResponse> roleResponsePage = roleService.pagingQueryRoles(roleQueryRequest);
        return Response.ok(roleResponsePage);
    }

    @DeleteMapping("/{roleId}")
    public Response<Void> removeRoles(@PathVariable("roleId") String roleId) {
        roleService.removeRole(roleId);
        return Response.ok();
    }
}

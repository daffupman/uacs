package io.daff.uacs.service.controller;

import io.daff.entity.Response;
import io.daff.uacs.service.entity.req.RoleRequest;
import io.daff.uacs.service.service.RoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}

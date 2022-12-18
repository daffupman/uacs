package io.daff.uacs.cms.controller;

import io.daff.uacs.cms.entity.req.AppInfoRequest;
import io.daff.uacs.cms.service.AppInfoService;
import io.daff.web.entity.Response;
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
@RequestMapping("/app-info")
public class AppInfoController {

    @Resource
    private AppInfoService appInfoService;

    @PutMapping
    public Response<Integer> modifyAppInfo(@RequestBody @Validated AppInfoRequest appInfoRequest) {
        Integer appInfoId = appInfoService.saveOrUpdateAppInfo(appInfoRequest);
        return Response.ok(appInfoId);
    }
}

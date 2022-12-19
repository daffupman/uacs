package io.daff.uacs.cms.controller;

import io.daff.uacs.cms.entity.req.UserThingsSortableQueryRequest;
import io.daff.uacs.cms.entity.req.UserThingsRequest;
import io.daff.uacs.cms.service.UserThingsService;
import io.daff.uacs.cms.entity.resp.UserThingsResponse;
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
 * @since 2021/5/20
 */
@RestController
@RequestMapping("/user-things")
public class UserThingsController {

    @Resource
    private UserThingsService userThingsService;

    @PutMapping
    public Response<Long> modifyUser(@RequestBody @Validated UserThingsRequest userThingsRequest) {
        Long userId = userThingsService.saveOrUpdateUserThings(userThingsRequest);
        return Response.ok(userId);
    }

    @GetMapping
    public Response<Page<UserThingsResponse>> pagingQueryUserThings(@NotNull(message = "请求参数为空")
                                                         @Validated UserThingsSortableQueryRequest userThingsSortableQueryRequest) {

        Page<UserThingsResponse> userThingsResponsePage = userThingsService.pagingQueryUserThings(userThingsSortableQueryRequest);
        return Response.ok(userThingsResponsePage);
    }

    @DeleteMapping("/{userId}")
    public Response<Void> removeUserThings(@PathVariable("userId") Long userId) {
        userThingsService.removeUserThings(userId);
        return Response.ok();
    }

    @GetMapping("/{userId}")
    public Response<UserThingsResponse> userThingsDetail(@PathVariable("userId") Long userId) {
        UserThingsResponse userThingsResponse = userThingsService.userThingsDetail(userId);
        return Response.ok(userThingsResponse);
    }
}

package io.daff.uacs.cms.controller;

import io.daff.uacs.cms.service.PassportService;
import io.daff.uacs.cms.entity.req.SignInRequest;
import io.daff.uacs.cms.entity.resp.SignInResponse;
import io.daff.web.entity.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author daff
 * @since 2021/5/21
 */
@RestController
@RequestMapping("/passport")
public class PassportController {

    @Resource
    private PassportService passportService;

    @PostMapping("/sign-in")
    public Response<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse response = passportService.signIn(signInRequest);
        return Response.ok(response);
    }
}

package io.daff.uacs.web.api;

import io.daff.entity.Response;
import io.daff.uacs.service.entity.req.SignInRequest;
import io.daff.uacs.service.entity.resp.SignInResponse;
import io.daff.uacs.service.service.PassportService;
import io.daff.uacs.web.anno.ApiVersion;
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
@ApiVersion("/v1")
public class PassportApi {

    @Resource
    private PassportService passportService;

    @PostMapping("/sign-in")
    public Response<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse response = passportService.signIn(signInRequest);
        return Response.ok(response);
    }
}

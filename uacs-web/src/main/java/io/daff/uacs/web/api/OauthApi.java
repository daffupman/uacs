package io.daff.uacs.web.api;

import io.daff.entity.Response;
import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.req.RevokeTokenRequest;
import io.daff.uacs.service.entity.req.UserProfileRequest;
import io.daff.uacs.service.entity.req.VerifyTokenRequest;
import io.daff.uacs.service.entity.resp.OAuthResponse;
import io.daff.uacs.service.entity.resp.UserProfileResponse;
import io.daff.uacs.service.service.OAuth2Service;
import io.daff.uacs.web.anno.AccessLimiter;
import io.daff.uacs.web.anno.ApiVersion;
import io.daff.uacs.web.anno.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author daff
 * @since 2021/5/24
 */
@RestController
@RequestMapping("/oauth")
@ApiVersion("v1")
public class OauthApi {

    @Autowired
    private OAuth2Service oAuth2Service;

    @PostMapping("/token")
    @AccessLimiter(limit = 2)
    public Response<OAuthResponse> fetchToken(@RequestBody @NotNull(message = "缺失必要参数")
                                              @Valid OAuthRequest oAuthRequest) {

        OAuthResponse oAuthTokenVo = oAuth2Service.fetchToken(oAuthRequest);
        return Response.ok(oAuthTokenVo);
    }

    @PostMapping("/profile")
    public Response<UserProfileResponse> userProfile(@RequestBody @NotNull(message = "缺失必要参数")
                                                     @Valid UserProfileRequest userProfileRequest) {

        UserProfileResponse userProfileResponse = oAuth2Service.userProfile(userProfileRequest);
        return Response.ok(userProfileResponse);
    }

    @PostMapping("/verify")
    public Response<Boolean> verifyToken(@RequestBody @NotNull(message = "缺失必要参数")
                                         @Valid VerifyTokenRequest verifyTokenRequest) {

        return Response.ok(oAuth2Service.verifyToken(verifyTokenRequest));
    }

    @PostMapping("/revoke")
    public Response<Boolean> revokeToken(@RequestBody @NotNull(message = "缺失必要参数")
                                         @Valid RevokeTokenRequest revokeTokenRequest) {

        return Response.ok(oAuth2Service.revokeToken(revokeTokenRequest));
    }
}

package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.req.RevokeTokenRequest;
import io.daff.uacs.service.entity.req.UserProfileRequest;
import io.daff.uacs.service.entity.req.VerifyTokenRequest;
import io.daff.uacs.service.entity.resp.AuthorizeRequest;
import io.daff.uacs.service.entity.resp.OAuthResponse;
import io.daff.uacs.service.entity.resp.UserProfileResponse;

/**
 * @author daffupman
 * @since 2020/7/25
 */
public interface OAuth2Service {

    OAuthResponse authorize(AuthorizeRequest authorizeRequest);

    OAuthResponse fetchToken(OAuthRequest oAuthRequest);

    boolean verifyToken(VerifyTokenRequest verifyTokenRequest);

    UserProfileResponse userProfile(UserProfileRequest userProfileRequest);

    boolean revokeToken(RevokeTokenRequest revokeTokenRequest);
}

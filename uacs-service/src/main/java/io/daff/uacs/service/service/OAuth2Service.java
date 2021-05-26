package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.req.UseProfileRequest;
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

    boolean verifyToken(String token);

    UserProfileResponse userProfile(UseProfileRequest useProfileRequest);
}

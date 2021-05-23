package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.OAuthRequest;
import io.daff.uacs.service.entity.resp.OAuthResponse;

/**
 * @author daff
 * @since 2021/5/21
 */
public interface PassportService {

    OAuthResponse signIn(OAuthRequest oAuthRequest);
}

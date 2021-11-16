package io.daff.uacs.cms.service;

import io.daff.uacs.cms.entity.req.SignInRequest;
import io.daff.uacs.cms.entity.resp.SignInResponse;

/**
 * @author daff
 * @since 2021/5/21
 */
public interface PassportService {

    SignInResponse signIn(SignInRequest signInRequest);
}

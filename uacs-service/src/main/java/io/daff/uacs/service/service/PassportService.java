package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.SignInRequest;
import io.daff.uacs.service.entity.resp.SignInResponse;

/**
 * @author daff
 * @since 2021/5/21
 */
public interface PassportService {

    SignInResponse signIn(SignInRequest signInRequest);
}

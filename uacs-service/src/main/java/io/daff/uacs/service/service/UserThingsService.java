package io.daff.uacs.service.service;

import io.daff.uacs.service.entity.req.UserThingsQueryRequest;
import io.daff.uacs.service.entity.req.base.UserThingsRequest;
import io.daff.uacs.service.entity.resp.UserThingsResponse;
import io.daff.uacs.service.entity.resp.base.Page;

/**
 * @author daff
 * @since 2021/5/20
 */
public interface UserThingsService {

    Long saveOrUpdateUserThings(UserThingsRequest userThingsRequest);

    Page<UserThingsResponse> pagingQueryUserThings(UserThingsQueryRequest userThingsQueryRequest);

    boolean removeUserThings(Long userId);
}

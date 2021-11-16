package io.daff.uacs.cms.service;

import io.daff.uacs.cms.entity.req.UserThingsRequest;
import io.daff.uacs.cms.entity.req.UserThingsSortableQueryRequest;
import io.daff.uacs.cms.entity.resp.UserThingsResponse;
import io.daff.uacs.service.entity.resp.base.Page;

/**
 * @author daff
 * @since 2021/5/20
 */
public interface UserThingsService {

    Long saveOrUpdateUserThings(UserThingsRequest userThingsRequest);

    Page<UserThingsResponse> pagingQueryUserThings(UserThingsSortableQueryRequest userThingsSortableQueryRequest);

    boolean removeUserThings(Long userId);

    UserThingsResponse userThingsDetail(Long userId);
}

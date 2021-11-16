package io.daff.uacs.cms.service;

import io.daff.uacs.cms.entity.req.AppInfoRequest;

/**
 * @author daff
 * @since 2021/11/16
 */
public interface AppInfoService {

    Integer saveOrUpdateAppInfo(AppInfoRequest appInfoRequest);
}

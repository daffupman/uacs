package io.daff.uacs.cms.service.impl;

import io.daff.consts.GlobalConstants;
import io.daff.enums.Hint;
import io.daff.exception.BaseException;
import io.daff.uacs.cms.entity.req.AppInfoRequest;
import io.daff.uacs.cms.service.AppInfoService;
import io.daff.uacs.cms.service.BaseService;
import io.daff.uacs.service.entity.po.AppInfo;
import io.daff.uacs.service.entity.po.Role;
import io.daff.uacs.service.mapper.AppInfoMapper;
import io.daff.util.StringHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * @author daff
 * @since 2021/11/16
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Resource
    private AppInfoMapper appInfoMapper;
    @Resource
    private BaseService baseService;
    @Resource
    private HttpServletRequest request;

    @Override
    public Integer saveOrUpdateAppInfo(AppInfoRequest appInfoRequest) {
        Long currUserId = Long.valueOf((String) request.getAttribute(GlobalConstants.CURRENT_LOGIN_USER));

        int appInfoId;

        AppInfo putAppInfo = AppInfo.builder().appName(appInfoRequest.getAppName())
                .grantType(appInfoRequest.getGrantType())
                .scope(appInfoRequest.getScope())
                .redirectUrl(appInfoRequest.getRedirectUrl())
                .updateBy(currUserId)
                .build();

        if (!StringUtils.isEmpty(appInfoRequest.getId())) {
            // 修改角色
            if (!baseService.userVisibleAppInfoList(currUserId, Collections.singletonList(appInfoRequest.getId()))) {
                throw new BaseException(Hint.AUTHORITY_FAILED, "用户权限不足");
            }
            putAppInfo.setUpdateBy(currUserId);
            appInfoId = appInfoMapper.update(putAppInfo);
        } else {
            // 新增角色
            putAppInfo.setCreateBy(currUserId);
            putAppInfo.setAppId(StringHelper.uuid().substring(0, 10));
            putAppInfo.setAppSecret(StringHelper.uuid().substring(0, 10));
            appInfoId = appInfoMapper.insert(putAppInfo);
        }

        // TODO 发送新的数据到kafka

        return appInfoId;
    }
}

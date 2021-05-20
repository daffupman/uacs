package io.daff.uacs.service.service.impl;

import com.github.pagehelper.PageInfo;
import io.daff.consts.SystemConstants;
import io.daff.uacs.core.util.IdUtil;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.entity.req.UserThingsQueryRequest;
import io.daff.uacs.service.entity.req.base.UserThingsRequest;
import io.daff.uacs.service.entity.resp.UserThingsResponse;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.uacs.service.mapper.UserThingsMapper;
import io.daff.uacs.service.service.UserThingsService;
import io.daff.uacs.service.util.PageUtil;
import io.daff.util.StrongCryptoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2021/5/20
 */
@Service
public class UserThingsServiceImpl implements UserThingsService {

    @Resource
    private UserThingsMapper userThingsMapper;
    @Resource
    private IdUtil idUtil;

    @Override
    public Long saveOrUpdateUserThings(UserThingsRequest userThingsRequest) {
        // TODO：登录功能完成后补充
        Long currUserId = 100000000L;
        String currUserHierarchyId = "h001";
        String currUserTopHierarchyId = "h001";

        int effectRows;

        UserThings putUserThings = UserThings.builder().id(userThingsRequest.getId())
                .nickName(userThingsRequest.getNickName())
                .avatarUrl(userThingsRequest.getAvatarUrl())
                .updateBy(currUserId).build();

        if (!StringUtils.isEmpty(userThingsRequest.getId())) {
            // 修改
            effectRows = userThingsMapper.batchUpdate(Collections.singletonList(putUserThings));
        } else {
            // 新增
            putUserThings.setId(idUtil.nextId());
            putUserThings.setName(userThingsRequest.getName());
            putUserThings.setMobilePhoneNo(userThingsRequest.getMobilePhoneNo());
            putUserThings.setEmail(userThingsRequest.getEmail());
            String defaultPassword = SystemConstants.DEFAULT_PASSWORD;
            String salt = StrongCryptoUtil.genSalt();
            String encrypt = StrongCryptoUtil.encrypt(defaultPassword, salt);
            putUserThings.setPassword(encrypt);
            putUserThings.setSalt(salt);
            putUserThings.setHierarchyId(currUserHierarchyId);
            putUserThings.setTopHierarchy(currUserTopHierarchyId);

            putUserThings.setCreateBy(currUserId);
            effectRows = userThingsMapper.batchInsert(Collections.singletonList(putUserThings));
        }
        return effectRows > 0 ? putUserThings.getId() : null;
    }

    @Override
    public Page<UserThingsResponse> pagingQueryUserThings(UserThingsQueryRequest userThingsQueryRequest) {
        PageUtil.startPage(userThingsQueryRequest, UserThingsResponse.class);
        List<UserThings> userThings = userThingsMapper.select(
                UserThings.builder()
                        .name(userThingsQueryRequest.getName())
                        .mobilePhoneNo(userThingsQueryRequest.getMobilePhoneNo())
                        .email(userThingsQueryRequest.getEmail())
                        .build()
        );
        PageInfo<UserThings> userThingsPageInfo = new PageInfo<>(userThings);
        List<UserThingsResponse> userThingsResponses = userThings.stream().map(UserThingsResponse::of).collect(Collectors.toList());
        return Page.of(userThingsPageInfo.getTotal(), userThingsResponses);
    }

    @Override
    public boolean removeUserThings(Long userId) {

        List<UserThings> userThings = userThingsMapper.selectByIds(Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(userThings)) {
            return false;
        }

        int effectRows = userThingsMapper.deleteByIds(Collections.singletonList(userId));
        return effectRows > 0;
    }
}

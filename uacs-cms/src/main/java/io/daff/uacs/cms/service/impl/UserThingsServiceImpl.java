package io.daff.uacs.cms.service.impl;

import com.github.pagehelper.PageInfo;
import io.daff.uacs.cms.entity.req.UserThingsRequest;
import io.daff.uacs.cms.entity.req.UserThingsSortableQueryRequest;
import io.daff.uacs.cms.entity.resp.UserThingsResponse;
import io.daff.uacs.cms.service.UserThingsService;
import io.daff.uacs.cms.util.PageUtil;
import io.daff.uacs.core.util.IdUtil;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.entity.resp.base.Page;
import io.daff.uacs.service.mapper.UserThingsMapper;
import io.daff.utils.crypto.StrongCryptoUtil;
import io.daff.web.consts.GlobalConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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

    @Transactional(rollbackFor = Exception.class)
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
            effectRows = userThingsMapper.update(putUserThings);
        } else {
            // 新增
            putUserThings.setId(idUtil.nextDistributeId());
            putUserThings.setName(userThingsRequest.getName());
            putUserThings.setMobilePhoneNo(userThingsRequest.getMobilePhoneNo());
            putUserThings.setEmail(userThingsRequest.getEmail());
            String defaultPassword = GlobalConstants.DEFAULT_PASSWORD;
            String salt = StrongCryptoUtil.genSalt();
            String encrypt = StrongCryptoUtil.encrypt(defaultPassword, salt);
            putUserThings.setPassword(encrypt);
            putUserThings.setSalt(salt);
            putUserThings.setHierarchyId(currUserHierarchyId);
            putUserThings.setTopHierarchy(currUserTopHierarchyId);

            putUserThings.setCreateBy(currUserId);
            effectRows = userThingsMapper.insert(putUserThings);
        }
        return effectRows > 0 ? putUserThings.getId() : null;
    }

    @Override
    public Page<UserThingsResponse> pagingQueryUserThings(UserThingsSortableQueryRequest userThingsSortableQueryRequest) {
        PageUtil.startPage(userThingsSortableQueryRequest, UserThingsResponse.class);
        List<UserThings> userThings = userThingsMapper.select(
                UserThings.builder()
                        .name(userThingsSortableQueryRequest.getName())
                        .mobilePhoneNo(userThingsSortableQueryRequest.getMobilePhoneNo())
                        .email(userThingsSortableQueryRequest.getEmail())
                        .build()
        );
        PageInfo<UserThings> userThingsPageInfo = new PageInfo<>(userThings);
        List<UserThingsResponse> userThingsResponses = userThings.stream().map(UserThingsResponse::of).collect(Collectors.toList());
        return Page.of(userThingsPageInfo.getTotal(), userThingsResponses);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeUserThings(Long userId) {

        UserThings userThings = userThingsMapper.selectById(userId);
        if (userThings == null) {
            return false;
        }

        int effectRows = userThingsMapper.deleteById(userId);
        return effectRows > 0;
    }

    @Override
    public UserThingsResponse userThingsDetail(Long userId) {
        UserThings userThings = userThingsMapper.selectById(userId);
        return UserThingsResponse.of(userThings);
    }
}

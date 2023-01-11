package io.daff.uacs.service.service;

import io.daff.uacs.core.consts.CacheKeys;
import io.daff.uacs.core.consts.CacheTtls;
import io.daff.uacs.service.entity.po.UserThings;
import io.daff.uacs.service.mapper.UserThingsMapper;
import io.daff.uacs.service.util.JacksonUtil;
import io.daff.uacs.service.util.SimpleRedisUtil;
import io.daff.utils.common.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author daff
 * @since 2022/12/25
 */
@Service
public class CacheService {

    @Resource
    private UserThingsMapper userThingsMapper;
    @Resource
    private SimpleRedisUtil simpleRedisUtil;

    /**
     * 获取用户信息
     */
    public UserThings getUserThings(Long userId) {

        UserThings userThings;

        String cacheKey = String.format(CacheKeys.USER_PROFILE_PREFIX, userId);
        String cached = simpleRedisUtil.get(cacheKey);
        if (StringUtil.hasText(cached)) {
            return JacksonUtil.stringToBean(cached, UserThings.class);
        } else {
            userThings = userThingsMapper.selectById(userId);
            simpleRedisUtil.set(cacheKey, JacksonUtil.beanToString(userThings), CacheTtls.DAY_1);
        }
        return userThings;
    }
}

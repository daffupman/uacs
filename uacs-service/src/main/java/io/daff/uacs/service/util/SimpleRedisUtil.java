package io.daff.uacs.service.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 简单的redis的工具类
 *
 * @author daffupman
 * @since 2020/8/30
 */
@Component
public class SimpleRedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置string类型的值
     */
    public void set(String key, String value) {
        set(key, value, -1);
    }

    /**
     * 设置string类型的值，同时设置过期时间，单位s，时间为负数的则设置为永不过期
     */
    public void set(String key, String value, long ttl) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        if (ttl > 0) {
            stringRedisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 获取string类型的值
     */
    public String get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 按key删除值
     */
    public void delete(List<String> keys) {
        if (StringUtils.isEmpty(keys)) { return; }
        stringRedisTemplate.delete(keys);
    }

    /**
     * 按key删除值
     */
    public void delete(String key) {
        if (StringUtils.isEmpty(key)) { return; }
        stringRedisTemplate.delete(key);
    }

    /**
     * 按key删除值
     */
    public Boolean exist(String key) {
        if (StringUtils.isEmpty(key)) { return false; }
        return stringRedisTemplate.hasKey(key);
    }
}

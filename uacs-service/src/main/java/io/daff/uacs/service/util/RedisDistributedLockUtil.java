package io.daff.uacs.service.util;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 基于redis实现的分布式锁。该分布式锁可以自动释放锁，必须使用try resource catch语法，使用示例：
 * <pre>
 * try (RedisDistributedLockUtil lock = new RedisDistributedLockUtil(stringRedisTemplate, "demo", 30)) {
 *     boolean locked = lock.lock();
 *     if (locked) {
 *         // 业务逻辑
 *     }
 * }
 * </pre>
 * 该分布式锁不可自动续期，建议使用redisson
 *
 * @author daff
 * @since 2022/12/14
 */
@SuppressWarnings("all")
public class RedisDistributedLockUtil implements AutoCloseable{

    private final StringRedisTemplate stringRedisTemplate;
    // 锁名称，不同的业务使用不同的锁
    private final String key;
    // 占用锁资源最大时长，单位秒
    private final String value;
    private final long expireTime;

    public RedisDistributedLockUtil(StringRedisTemplate stringRedisTemplate, String key, long expireTime) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.key = key;
        // 每个线程存储的值都是唯一的，避免释放锁时释放的是其他线程的锁
        this.value = UUID.randomUUID().toString();
        this.expireTime = expireTime;
    }

    /**
     * 获取锁
     *
     * @return  是否获取锁成功
     */
    public boolean lock() {

        String value = UUID.randomUUID().toString();
        RedisCallback<Boolean> redisCallback = connection -> connection.set(
                RedisSerializer.string().serialize(key),
                RedisSerializer.string().serialize(value),
                // 设置过期时间
                Expiration.seconds(expireTime),
                // 设置NX
                RedisStringCommands.SetOption.ifAbsent()
        );

        return Boolean.TRUE.equals(stringRedisTemplate.execute(redisCallback));
    }

    /**
     * 释放锁
     *
     * @return 是否释放锁成功
     */
    private boolean unlock() {
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n"
                + "  return redis.call(\"del\", KEYS[1])\n"
                + "else\n"
                + "  return 0\n"
                + "end";
        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        List<String> keys = Collections.singletonList(key);
        return Boolean.TRUE.equals(stringRedisTemplate.execute(redisScript, keys, value));
    }

    @Override
    public void close() {
        unlock();
    }
}

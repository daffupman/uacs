package io.daff.uacs.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * redis相关配置
 *
 * @author daff
 * @since 2022/12/15
 */
@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Boolean> rateLimitScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("ratelimit.lua"));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
}

package io.daff.uacs.web.aspect;

import com.google.common.collect.Lists;
import io.daff.uacs.web.anno.AccessLimiter;
import io.daff.utils.common.StringUtil;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 限流切面
 *
 * @author daff
 * @since 2022/12/15
 */
@Aspect
@Component
public class AccessLimiterAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisScript<Boolean> rateLimiterScript;
    @Resource
    private Environment environment;


    @Pointcut("@annotation(io.daff.uacs.web.anno.AccessLimiter)")
    public void withAccessLimiter() {
    }

    @Around("withAccessLimiter()")
    public Object accessLimit(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        AccessLimiter anno = method.getAnnotation(AccessLimiter.class);
        String limitKey = anno.key();
        int limit = anno.limit();

        if (!StringUtil.hasText(limitKey)) {
            // 默认生成规则 -> 服务名:类名:方法名
            String appName = environment.getProperty("spring.application.name");
            String clzName = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            limitKey = String.format("ratelimit:%s:%s:%s", appName, clzName, methodName);
        }

        // 查询redis限流
        Boolean passed = stringRedisTemplate.execute(rateLimiterScript, Lists.newArrayList(limitKey), String.valueOf(limit));
        if (Boolean.FALSE.equals(passed)) {
            throw new BaseException(Hint.SYSTEM_ERROR, "访问过于频繁，请稍后重试！");
        }

        return pjp.proceed();
    }
}

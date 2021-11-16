package io.daff.uacs.web.aspect;

import io.daff.enums.Hint;
import io.daff.exception.BaseException;
import io.daff.uacs.service.util.SimpleRedisUtil;
import io.daff.uacs.web.anno.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 * 接口幂等
 *
 * @author daffupman
 * @since 2021/11/15
 */
@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class IdempotentAspect {

    @Resource
    private SimpleRedisUtil simpleRedisUtil;
    @Resource
    private HttpServletRequest request;

    private static final String IDEMPOTENT_CACHE_KEY_FORMAT = "idempotent:%s:%s:%s";
    private static final String ON_CALL = "true";

    // within指示器实现了匹配那些类型上标记了@RestController注解的方法
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void idempotentApi() {
    }

    @Around("idempotentApi()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Annotation[] annotations = signature.getMethod().getAnnotations();
        if (annotations.length <= 0) {
            return pjp.proceed();
        }

        boolean isIdempotentApi = false;
        for (Annotation annotation : annotations) {
            if (Idempotent.class.equals(annotation.annotationType())) {
                isIdempotentApi = true;
                break;
            }
        }

        if (!isIdempotentApi) {
            return pjp.proceed();
        }

        String idempotentCacheKey = String.format(IDEMPOTENT_CACHE_KEY_FORMAT,
                signature.getDeclaringType().getSimpleName() + "." + signature.getMethod().getName(),
                request.getHeader("app_id"), request.getHeader("timestamp"));

        Object proceed;
        try {
            synchronized (IdempotentAspect.class) {
                if (simpleRedisUtil.exist(idempotentCacheKey)) {
                    throw new BaseException(Hint.SYSTEM_ERROR, "接口调用中，请稍后再试");
                }
                simpleRedisUtil.set(idempotentCacheKey, ON_CALL);
            }
            proceed = pjp.proceed();
        } finally {
            simpleRedisUtil.delete(idempotentCacheKey);
        }
        return proceed;
    }
}

package io.daff.uacs.web.interceptor;

import io.daff.uacs.service.util.SimpleRedisUtil;
import io.daff.uacs.web.anno.Idempotent;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 接口幂等
 *
 * @author daffupman
 * @since 2021/11/15
 */
@Component
public class IdempotentInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private SimpleRedisUtil simpleRedisUtil;

    private static final String IDEMPOTENT_CACHE_KEY_FORMAT = "idempotent:%s:%s:%s";
    private static final String ON_CALL = "true";
    private final ThreadLocal<String> idempotentCacheKeyThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return true;
        }

        String idempotentCacheKey = String.format(IDEMPOTENT_CACHE_KEY_FORMAT,
                method.getDeclaringClass().getSimpleName() + "." + method.getName(),
                request.getHeader("app_id"), request.getHeader("timestamp"));
        idempotentCacheKeyThreadLocal.set(idempotentCacheKey);

        synchronized (IdempotentInterceptor.class) {
            if (simpleRedisUtil.exist(idempotentCacheKey)) {
                throw new BaseException(Hint.SYSTEM_ERROR, "接口调用中，请稍后再试");
            }
            simpleRedisUtil.set(idempotentCacheKey, ON_CALL, 60 * 5);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        simpleRedisUtil.delete(idempotentCacheKeyThreadLocal.get());
        idempotentCacheKeyThreadLocal.remove();
    }
}

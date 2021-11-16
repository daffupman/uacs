package io.daff.uacs.web.aspect;

import io.daff.exception.ParamValidateException;
import io.daff.uacs.service.entity.req.base.Signature;
import io.daff.uacs.service.service.cache.AppInfoLocalData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口验签
 *
 * @author daffupman
 * @since 2021/11/15
 */
@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SignatureAspect {

    @Resource
    private AppInfoLocalData appInfoLocalData;
    @Resource
    private HttpServletRequest request;

    // within指示器实现了匹配那些类型上标记了@RestController注解的方法
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerBean() {
    }

    @Around("controllerBean()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {

        // 验签
        Map<String, Object> params = flatParams(pjp.getArgs());
        Signature signature = new Signature().build(request);

        if (signature.isDebug()) {
            return pjp.proceed();
        }

        String secret = appInfoLocalData.getAppSecretById(signature.getAppId());
        if (secret == null) {
            throw new ParamValidateException("无效的app_id");
        }
        if (!signature.verify(params, secret)) {
            throw new ParamValidateException("签名验证错误");
        }
        // 重放攻击验证
        Long timestamp = signature.getTimestamp();

        if (System.currentTimeMillis() - timestamp > 1000 * 60 * 500) {
            throw new ParamValidateException("接口调用已过期");
        }
        return pjp.proceed();
    }

    private Map<String, Object> flatParams(Object[] args) {
        Map<String, Object> params = new HashMap<>();
        if (args == null || args.length == 0) {
            return params;
        }
        for (Object arg : args) {
            Field[] fields = arg.getClass().getDeclaredFields();
            if (fields.length == 0) {
                return params;
            }
            for (Field field : fields) {
                String fieldName = field.getName();
                try {
                    params.put(fieldName, arg.getClass().getDeclaredField(fieldName));
                } catch (NoSuchFieldException e) {
                }
            }
        }
        return params;
    }
}

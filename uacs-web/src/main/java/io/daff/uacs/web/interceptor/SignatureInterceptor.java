package io.daff.uacs.web.interceptor;

import io.daff.exception.ParamValidateException;
import io.daff.uacs.service.entity.req.base.Signature;
import io.daff.uacs.service.service.cache.AppInfoLocalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口验签
 *
 * @author daffupman
 * @since 2021/11/15
 */
@Component
@Slf4j
public class SignatureInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private AppInfoLocalData appInfoLocalData;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Parameter[] parameters = handlerMethod.getMethod().getParameters();

        // 验签
        Map<String, Object> params = flatParams(parameters);
        Signature signature = new Signature().build(request);

        if (signature.isDebug()) {
            return true;
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
        return true;
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

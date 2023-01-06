package io.daff.uacs.web.interceptor;

import io.daff.uacs.service.entity.req.sign.Signature;
import io.daff.uacs.service.entity.req.sign.secret.SecretStorage;
import io.daff.uacs.service.service.cache.UacsBizDataLoader;
import io.daff.web.exception.ParamValidateException;
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
 * 接口验签拦截器
 *
 * @author daffupman
 * @since 2021/11/15
 */
@Component
public class SignatureInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private SecretStorage secretStorage;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 生成签名对象
        Signature signature = new Signature().build(request);

        // 调试模式
        if (signature.isDebug()) {
            return true;
        }

        // 重放攻击验证
        if (signature.replay()) {
            throw new ParamValidateException("接口调用已过期");
        }

        String secret = secretStorage.getSecretByAppId(signature.getAppId());
        if (secret == null) {
            throw new ParamValidateException("无效的app_id");
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        Map<String, Object> params = flatParams(parameters);
        if (!signature.verify(params, secret)) {
            throw new ParamValidateException("签名验证错误");
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
                    // ...
                }
            }
        }
        return params;
    }
}

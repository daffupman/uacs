package io.daff.uacs.web.interceptor;

import io.daff.uacs.web.interceptor.sign.ApiAccessAuthenticator;
import io.daff.uacs.web.interceptor.sign.Signature;
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
    private ApiAccessAuthenticator apiAccessAuthenticator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        Map<String, Object> params = flatParams(parameters);

        Signature signature = new Signature.Builder().appId(request.getHeader(Signature.HEADER_APP_ID))
                .timestamp(request.getHeader(Signature.HEADER_SIGNATURE))
                .rawSignature(request.getHeader(Signature.HEADER_SIGNATURE))
                .debug(request.getHeader(Signature.HEADER_DEBUG))
                .bizParams(params)
                .build();
        // 验证
        apiAccessAuthenticator.auth(signature);

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

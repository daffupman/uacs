package io.daff.uacs.web.interceptor;

import io.daff.uacs.web.context.ApiBodyContext;
import io.daff.web.entity.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author daff
 * @since 2021/11/18
 */
@SuppressWarnings("rawtypes")
@ControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Response> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Response beforeBodyWrite(Response body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        ApiBodyContext.set(body);
        return body;
    }
}

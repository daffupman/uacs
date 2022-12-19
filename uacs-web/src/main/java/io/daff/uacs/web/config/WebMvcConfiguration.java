package io.daff.uacs.web.config;

import io.daff.uacs.web.handler.ApiVersionHandlerMapping;
import io.daff.uacs.web.interceptor.IdempotentInterceptor;
import io.daff.uacs.web.interceptor.SignatureInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;

/**
 * SpringMvc相关配置
 *
 * @author daffupman
 * @since 2020/7/14
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer, WebMvcRegistrations {

    @Resource
    private SignatureInterceptor signatureInterceptor;
    @Resource
    private IdempotentInterceptor idempotentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.signatureInterceptor).order(2).addPathPatterns("/**");
        registry.addInterceptor(this.idempotentInterceptor).order(3).addPathPatterns("/**");
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiVersionHandlerMapping();
    }
}

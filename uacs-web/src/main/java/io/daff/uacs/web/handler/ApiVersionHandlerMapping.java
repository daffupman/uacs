package io.daff.uacs.web.handler;

import io.daff.uacs.web.anno.ApiVersion;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * api版本HandlerMapping：通过继承RequestMappingHandlerMapping，拼接上版本号
 *
 * @author daffupman
 * @since 2020/8/15
 */
public class ApiVersionHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        Class<?> controllerClass = method.getDeclaringClass();
        // 类上的api注解
        ApiVersion apiVersionOnClass = AnnotationUtils.findAnnotation(controllerClass, ApiVersion.class);
        // 方法上的api注解
        ApiVersion apiVersionOnMethod = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if (apiVersionOnMethod != null) {
            // 方法上的优先
            apiVersionOnClass = apiVersionOnMethod;
        }

        String apiVersionUrlPattern = apiVersionOnClass == null ? "" : apiVersionOnClass.value();

        PatternsRequestCondition apiVersionPattern = new PatternsRequestCondition(apiVersionUrlPattern);
        PatternsRequestCondition oldPatterns = mapping.getPatternsCondition();
        PatternsRequestCondition combinedPattern = apiVersionPattern.combine(oldPatterns);
        // 重新构建RequestMappingInfo
        mapping = new RequestMappingInfo(
                mapping.getName(),
                combinedPattern,
                mapping.getMethodsCondition(),
                mapping.getParamsCondition(),
                mapping.getHeadersCondition(),
                mapping.getConsumesCondition(),
                mapping.getProducesCondition(),
                mapping.getCustomCondition()
        );

        super.registerHandlerMethod(handler, method, mapping);
    }
}

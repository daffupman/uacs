package io.daff.uacs.web.anno;

import java.lang.annotation.*;

/**
 * 限流
 *
 * @author daff
 * @since 2022/12/15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimiter {

    /**
     * 限流对象的名称，默认 应用名称:类名:方法名
     */
    String key() default "";

    /**
     * 每秒限流的值，默认10
     */
    int limit() default 10;
}

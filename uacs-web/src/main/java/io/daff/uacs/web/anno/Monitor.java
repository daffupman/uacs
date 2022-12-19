package io.daff.uacs.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统监控系统
 *
 * @author daffupman
 * @since 2020/8/31
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {

    /**
     * 记录方法执行成功的耗时，默认开启
     */
    boolean recordExecuteSuccessTimeCost() default true;

    /**
     * 记录方法执行失败的耗时，默认开启
     */
    boolean recordExecuteFailureTimeCost() default true;

    /**
     * 记录方法的请求参数
     */
    boolean recordRequestParams() default true;

    /**
     * 记录方法的返回值
     */
    boolean recordReturnValue() default true;

    /**
     * 记录方法抛出的异常
     */
    boolean recordException() default true;

    /**
     * 出现异常后，忽略异常，返回默认值
     */
    boolean ignoreException() default true;
}

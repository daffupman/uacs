package io.daff.uacs.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 版本号统一控制注解
 *
 * @author daffupman
 * @since 2020/8/15
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

    /**
     * 填写版本号，一般填写：v1、v2等
     */
    String value();
}

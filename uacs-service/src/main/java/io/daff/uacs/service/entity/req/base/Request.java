package io.daff.uacs.service.entity.req.base;

import org.springframework.util.StringUtils;

/**
 * @author daff
 * @since 2021/5/17
 */
public interface Request {

    /**
     * 对参数的检查：如果是分页查询，需要检查sort的值是否合法，如果有其他的校验，可以写在这里
     */
    default void validate() {}

    /**
     * 对String类型的字段做trim操作
     */
    default String trim(String field) {
        return StringUtils.isEmpty(field) ? null : field.trim();
    }
}

package io.daff.uacs.cms.util;

import com.github.pagehelper.PageHelper;
import io.daff.uacs.cms.entity.req.base.QueryRequest;
import io.daff.uacs.cms.entity.req.base.SortableQueryRequest;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
import io.daff.web.exception.ParamValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * pageHelper工具类
 *
 * @author daff
 * @since 2020/8/22
 */
@Slf4j
public class PageUtil {

    /**
     * 对QueryForm启用分页工具
     */
    public static void startPage(QueryRequest queryRequest, @NotNull Class<?> clazz) {

        Integer pageNum = queryRequest.getPageNum();
        Integer pageSize = queryRequest.getPageSize();

        if (pageNum == null && pageSize == null) {
            return;
        }

        if (pageNum == null || pageSize == null) {
            throw new ParamValidateException("页码与页大小需要同时填写，请补充填写");
        }

        if (queryRequest instanceof SortableQueryRequest) {

            SortableQueryRequest sortedQueryForm = (SortableQueryRequest) queryRequest;
            String sort = sortedQueryForm.getSort();
            String ordinal = sortedQueryForm.getOrdinal();
            if (!StringUtils.isEmpty(sort) && StringUtils.isEmpty(ordinal) ||
                    StringUtils.isEmpty(sort) && !StringUtils.isEmpty(ordinal)) {
                throw new ParamValidateException("排序字段与排序规则需要同时填写，请补充填写");
            }

            if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(ordinal)) {
                // 检查sort字段的类型是否是String类型
                boolean isString = false;
                try {
                    Class<?> clz = Class.forName(clazz.getName());
                    for (Field declaredField : clz.getDeclaredFields()) {
                        if (sort.equalsIgnoreCase(declaredField.getName())) {
                            if (declaredField.getType().equals(String.class)) {
                                isString = true;
                            }
                            break;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("传入的class对象不存在", e);
                    throw new BaseException(Hint.SYSTEM_ERROR);
                }

                String orderByClause = isString ?
                        "CONVERT(" + sort + " USING GBK)" + " " + ordinal : sort + " " + ordinal;
                PageHelper.startPage(pageNum, pageSize, orderByClause);
                return;
            }
        }

        PageHelper.startPage(pageNum, pageSize);

    }
}

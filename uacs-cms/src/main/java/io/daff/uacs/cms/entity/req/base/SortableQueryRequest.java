package io.daff.uacs.cms.entity.req.base;

import io.daff.valid.anno.Limit;
import io.daff.web.exception.ParamValidateException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author daff
 * @since 2021/5/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class SortableQueryRequest extends QueryRequest {

    @ApiModelProperty("排序字段")
    private String sort = "create_at";

    @ApiModelProperty("排序规则")
    @Limit(value = {"asc", "desc"}, message = "排序规则不合法，请重新输入")
    private String ordinal = "asc";

    /**
     * 检查排序字段是否合法，需要根据业务需要传入合法的排序字段
     */
    protected void handleSort(String... allowFields) {
        if (!StringUtils.isEmpty(this.sort) && allowFields.length > 0
                && !Arrays.asList(allowFields).contains(this.sort)) {
            throw new ParamValidateException("排序字段不合法，请重新输入");
        }
    }
}

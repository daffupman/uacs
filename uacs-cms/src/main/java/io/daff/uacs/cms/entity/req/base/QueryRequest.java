package io.daff.uacs.cms.entity.req.base;

import io.daff.uacs.core.entity.req.Request;
import io.daff.util.SqlLikeEscapeUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public abstract class QueryRequest implements Request {

    @ApiModelProperty("页码")
    @DecimalMin(value = "1", message = "页码最小为1，请重新输入")
    @DecimalMax(value = "1000", message = "页码最大为1000，请重新输入")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    @DecimalMin(value = "1", message = "每页条数最小为1，请重新输入")
    @DecimalMax(value = "1000", message = "每页条数最大为1000，请重新输入")
    private Integer pageSize = 10;

    /**
     * 对String类型的字段做escape和trim操作
     */
    public String escapeAndTrimForString(String field) {

        return StringUtils.isEmpty(field) ? null :
                SqlLikeEscapeUtils.escapeSpecialChar(field.trim());
    }
}

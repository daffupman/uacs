package io.daff.uacs.service.entity.req;

import io.daff.uacs.service.entity.req.base.QueryRequest;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class PermissionQueryRequest extends QueryRequest {

    private String id;
    private String code;
    private String desc;
    private String type;
    private String status;

    @Override
    public void validate() {
        this.code = StringUtils.isEmpty(code) ? null : escapeAndTrimForString(code);
        this.desc = StringUtils.isEmpty(desc) ? null : escapeAndTrimForString(desc);
        this.type = StringUtils.isEmpty(type) ? null : escapeAndTrimForString(type);
        this.status = StringUtils.isEmpty(status) ? null : escapeAndTrimForString(status);
    }
}

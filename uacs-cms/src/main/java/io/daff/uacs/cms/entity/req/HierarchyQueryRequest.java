package io.daff.uacs.cms.entity.req;

import io.daff.uacs.cms.entity.req.base.QueryRequest;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class HierarchyQueryRequest extends QueryRequest {

    private String id;
    private String name;

    @Override
    public void validate() {
        this.name = StringUtils.isEmpty(name) ? null : escapeAndTrimForString(name);
    }
}

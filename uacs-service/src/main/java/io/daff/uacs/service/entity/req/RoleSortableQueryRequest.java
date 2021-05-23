package io.daff.uacs.service.entity.req;

import io.daff.uacs.service.entity.req.base.SortableQueryRequest;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class RoleSortableQueryRequest extends SortableQueryRequest {

    private String name;

    @Override
    public void validate() {
        this.name = StringUtils.isEmpty(name) ? null : escapeAndTrimForString(name);

    }
}

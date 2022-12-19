package io.daff.uacs.cms.entity.req;

import io.daff.uacs.cms.entity.req.base.SortableQueryRequest;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class UserThingsSortableQueryRequest extends SortableQueryRequest {

    private String name;
    private String mobilePhoneNo;
    private String email;

    @Override
    public void validate() {
        this.name = StringUtils.isEmpty(name) ? null : escapeAndTrimForString(name);
        this.mobilePhoneNo = StringUtils.isEmpty(mobilePhoneNo) ? null : escapeAndTrimForString(mobilePhoneNo);
        this.email = StringUtils.isEmpty(email) ? null : escapeAndTrimForString(email);
    }
}

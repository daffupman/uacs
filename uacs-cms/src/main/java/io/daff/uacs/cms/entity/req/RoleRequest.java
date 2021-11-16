package io.daff.uacs.cms.entity.req;

import io.daff.uacs.core.entity.req.Request;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class RoleRequest implements Request {

    private String id;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String desc;

    public void validate() {
        this.name = StringUtils.isEmpty(name) ? null : trim(name);
        this.desc = StringUtils.isEmpty(desc) ? null : trim(desc);
    }
}

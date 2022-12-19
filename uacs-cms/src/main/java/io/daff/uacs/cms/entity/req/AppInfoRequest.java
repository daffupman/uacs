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
public class AppInfoRequest implements Request {

    private Integer id;

    @NotBlank(message = "客户端名称不能为空")
    private String appName;

    @NotBlank(message = "授权类型不能为空")
    private String grantType;

    @NotBlank(message = "权限范围不能为空")
    private String scope;

    @NotBlank(message = "重定向地址不能为空")
    private String redirectUrl;

    private String description;

    public void validate() {
        this.appName = StringUtils.isEmpty(appName) ? null : trim(appName);
        this.grantType = StringUtils.isEmpty(grantType) ? null : trim(grantType);
        this.scope = StringUtils.isEmpty(scope) ? null : trim(scope);
        this.description = StringUtils.isEmpty(description) ? null : trim(description);
    }
}

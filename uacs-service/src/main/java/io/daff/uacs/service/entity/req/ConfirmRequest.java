package io.daff.uacs.service.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author daffupman
 * @since 2020/7/27
 */
@Data
@ApiModel("确认授权表单")
public class ConfirmRequest {

    @ApiModelProperty("客户端id")
    @NotBlank(message = "客户端id必填")
    private String appId;

    @ApiModelProperty("客户端密钥")
    @NotBlank(message = "客户端密钥必填")
    private String appSecret;

    @ApiModelProperty("是否确认授权")
    @NotNull(message = "是否确认授权必填")
    private Boolean confirm;

    @ApiModelProperty("权限范围，确认授权时必填")
    private List<String> scopes;
}

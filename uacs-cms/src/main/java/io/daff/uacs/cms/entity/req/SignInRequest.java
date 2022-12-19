package io.daff.uacs.cms.entity.req;

import io.daff.valid.anno.Limit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author daff
 * @since 2021/5/21
 */
@ApiModel("登录请求")
@Data
public class SignInRequest {

    @ApiModelProperty("登录类型：ap - 帐密登录，mc - 手机号验证码登录")
    @NotBlank(message = "登录类型必填")
    @Limit(value = {"ap", "mc"}, message = "输入的登录类型错误")
    private String type;

    @ApiModelProperty("用户账号：可填手机号/登录名/邮箱")
    @NotBlank(message = "用户账号必填")
    private String account;

    @ApiModelProperty(value = "密码", notes = "登录类型为帐密登录时必填")
    private String password;

    @ApiModelProperty(value = "验证码", notes = "登录类型为手机号验证码登录时必填")
    private String code;
}

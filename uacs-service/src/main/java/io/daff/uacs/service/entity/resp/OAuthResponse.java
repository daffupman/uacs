package io.daff.uacs.service.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author daff
 * @since 2021/5/24
 */
@Data
public class OAuthResponse {

    @ApiModelProperty("访问令牌")
    private String accessToken;

    /**
     * 令牌类型：可以是bearer类型或mac类型。
     */
    @ApiModelProperty("令牌类型")
    private String tokenType;

    /**
     * 多长时间后令牌失效
     */
    @ApiModelProperty("有效时间")
    private Long expiresIn;

    @ApiModelProperty("刷新令牌")
    private String refreshToken;

    /**
     * 如果客户端需要的权限范围和之前申请的权限范围一致，可以不返回
     */
    @ApiModelProperty("权限范围")
    private List<String> scope;

    /**
     * 授权码许可下会颁布code
     */
    @ApiModelProperty("授权码")
    private String code;

    /**
     * 授权服务不作处理，原样返回
     */
    @ApiModelProperty("state值")
    private String state;

    @ApiModelProperty("用户id")
    private Long userId;
}

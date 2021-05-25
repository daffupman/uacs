package io.daff.uacs.service.entity.req;

import io.daff.anno.Limit;
import io.daff.uacs.core.enums.GrantTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

/**
 * @author daff
 * @since 2021/5/24
 */
@Data
public class OAuthRequest {

    @NotBlank(message = "客户端id不能为空")
    private String appId;

    @NotBlank(message = "客户端密钥不能为空")
    private String appSecret;

    /**
     * 此次获取token所使用的授权许可
     * - authorization_code ： 授权码许可
     * - password ：资源拥有者凭据许可
     * - client_credentials：客户端凭据许可
     * - refresh_token: 更新令牌
     */
    @NotBlank(message = "授权类型不能为空")
    @Limit(
            value = {"authorization_code", "password", "client_credentials", "refresh_token"},
            message = "参数grantType只支持：authorization_code，password，client_credentials，refresh_token。"
    )
    private String grantType;

    /**
     * 授权码许可中从/authorize获取的授权码，授权码使用过会一定要删除
     */
    private String code;

    /**
     * 授权服务器完成操作（接受或拒绝）后重定向到的uri
     */
    @URL(message = "重定向地址格式错误")
    private String redirectUri;

    private String username;

    private String password;

    private String refreshToken;

    /**
     * 此次客户端请求所需要的权限范围
     */
    private String scope;

    public GrantTypeEnum getGrantTypeEnum() {
        for (GrantTypeEnum gt : GrantTypeEnum.values()) {
            if (gt.value().equals(grantType)) {
                return gt;
            }
        }
        return null;
    }
}

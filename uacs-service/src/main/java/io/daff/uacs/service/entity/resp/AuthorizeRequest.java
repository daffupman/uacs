package io.daff.uacs.service.entity.resp;

import io.daff.anno.Limit;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * OAuth授权接口参数模型
 *
 * @author daffupman
 * @since 2020/7/25
 */
@Data
public class AuthorizeRequest {

    /**
     * 第三方客户端要求授权服务器返回的影响类型：
     * <ul>
     *     <li> code: 要求返回授权码，该授权码需要设置过期时间，且只能使用一次（授权码许可），接着会再携带一些必要的参数去调用/token接口获取访问令牌</li>
     *     <li> token: 要求直接返回访问令牌（隐式许可）</li>
     * </ul>
     */
    @NotBlank(message = "响应类型不能为空")
    @Limit(value = {"code", "token"}, message = "响应类型只支持code、token")
    private String responseType;

    @NotBlank(message = "客户端id必填")
    private String appId;

    private String appSecret;

    /**
     * 授权服务器完成操作（接受或拒绝）后重定向到的uri
     */
    @NotBlank(message = "重定向地址不能为空")
    @URL(message = "重定向地址格式错误")
    private String redirectUri;

    /**
     * 第三方需要的权限范围，如：read，write等，以逗号分隔
     */
    private List<String> scopes;

    /**
     * state值授权服务不做处理，是第三方客户端传入的，授权服务也会原样返回。而第三方客户端需要对state做校验，防止crsf攻击
     */
    private String state;

    /**
     * 颁发授权码时必填
     */
    private String accessToken;
}

package io.daff.uacs.service.entity.dto;

import io.daff.uacs.core.enums.GrantTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * OAuth授权中token中需要携带的信息
 *
 * @author daffupman
 * @since 2020/7/27
 */
@Data
public class OAuthExtraInfo {

    /**
     * 在那种授权模式下生成的token，必须，参考 {@link GrantTypeEnum}
     */
    @NotNull
    private GrantTypeEnum grantType;

    /**
     * 资源拥有者id，在授权码许可和资源拥有者许可下，此字段必须
     */
    private String resourceOwnerId;

    /**
     * 资源拥有者密钥，在授权码许可和资源拥有者许可下，此字段必须
     */
    private String resourceOwnerSecret;

    /**
     * 客户端id，必须
     */
    @NotNull
    private String appId;

    /**
     * 客户端密钥，必须
     */
    @NotNull
    private String appSecret;

    /**
     * token的存活时间（min）
     */
    private Long ttl;

    /**
     * token的过期时间
     */
    private Date expireDate;

    /**
     * token的类型：访问令牌（A），刷新令牌（R）
     */
    private String tokenType;
}

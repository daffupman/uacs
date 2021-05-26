package io.daff.uacs.service.entity.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author daff
 * @since 2021/5/26
 */
@Data
public class UseProfileRequest {

    @NotBlank(message = "用户id参数缺失")
    private Long userId;

    @NotBlank(message = "访问令牌")
    private String accessToken;
}

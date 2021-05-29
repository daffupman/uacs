package io.daff.uacs.service.entity.req;

import io.daff.uacs.service.entity.resp.UserProfileResponse;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author daff
 * @since 2021/5/26
 */
@Data
public class VerifyTokenRequest extends UserProfileRequest {
}

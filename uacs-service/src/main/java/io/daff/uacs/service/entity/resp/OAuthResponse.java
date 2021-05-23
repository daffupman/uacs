package io.daff.uacs.service.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author daff
 * @since 2021/5/21
 */
@Data
@AllArgsConstructor
public class OAuthResponse {

    private Long id;
    private String name;
    private String nickName;
    private String accessToken;
    private String refreshToken;
}

package io.daff.uacs.service.entity.req;

import lombok.Data;

/**
 * @author daffupman
 * @since 2020/7/27
 */
@Data
public class ConfirmResponse {

    private String userId;
    private String appId;

    public ConfirmResponse(String userId, String appId) {
        this.userId = userId;
        this.appId = appId;
    }
}

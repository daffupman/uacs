package io.daff.uacs.cms.entity.resp;

import lombok.Data;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class PermissionResponse {

    private String code;
    private String desc;
    private String type;
    private String state;
    private String createBy;
}

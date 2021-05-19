package io.daff.uacs.service.entity.resp;

import lombok.Data;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
public class UserThingsResponse {

    private String name;
    private String nickName;
    private String mobilePhoneNo;
    private String email;
    private String status;
    private String createBy;
}

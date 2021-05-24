package io.daff.uacs.service.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daff
 * @since 2021/5/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserThingsRequest {

    private Long id;
    private String name;
    private String nickName;
    private String mobilePhoneNo;
    private String email;
    private String password;
    private String avatarUrl;
    private String hierarchy;
    private String topHierarchy;
    private String status;
    private String createBy;
}

package io.daff.uacs.service.entity.po;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * user_things琛ㄧ殑瀹炰綋绫籠n *
 * @author wangzhengjin
 * @since 2021/05/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserThings {
    private Long id;

    private String name;

    private String nickName;

    private String mobilePhoneNo;

    private String email;

    private String password;

    private String salt;

    private String avatarUrl;

    private String hierarchyId;

    private String topHierarchy;

    private Byte status;

    private Boolean mobilePhoneNoVerified;

    private Boolean emailVerified;

    private Long createBy;

    private Long updateBy;

    private Date createAt;

    private Date updateAt;

    private String detail;
}
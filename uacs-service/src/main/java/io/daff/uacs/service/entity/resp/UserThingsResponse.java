package io.daff.uacs.service.entity.resp;

import io.daff.uacs.service.entity.po.UserThings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daff
 * @since 2021/5/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserThingsResponse {

    private String name;
    private String nickName;
    private String mobilePhoneNo;
    private String email;
    private String status;
    private String createBy;

    public static UserThingsResponse of(UserThings userThings) {
        String status;
        switch (userThings.getStatus()) {
            case -1: status = "注销"; break;
            case 0: status = "禁用"; break;
            case 1: status = "正常"; break;
            case 2: status = "锁定"; break;
            case 3: status = "过期"; break;
            default: status = "未知"; break;
        }
        return UserThingsResponse.builder()
                .name(userThings.getName())
                .nickName(userThings.getNickName())
                .mobilePhoneNo(userThings.getMobilePhoneNo())
                .email(userThings.getEmail())
                .status(status)
                .build();
    }
}

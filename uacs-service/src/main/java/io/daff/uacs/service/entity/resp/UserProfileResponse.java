package io.daff.uacs.service.entity.resp;

import io.daff.uacs.service.entity.po.UserThings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daff
 * @since 2021/5/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String nickName;

    public static UserProfileResponse of(UserThings userThings) {
        return UserProfileResponse.builder().id(userThings.getId())
                .name(userThings.getName())
                .nickName(userThings.getNickName()).build();
    }
}

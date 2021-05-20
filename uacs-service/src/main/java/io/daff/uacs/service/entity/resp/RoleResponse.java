package io.daff.uacs.service.entity.resp;

import io.daff.uacs.service.entity.po.Role;
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
public class RoleResponse {

    private String id;
    private String name;
    private String type;
    private String createBy;

    public static RoleResponse of(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .type(role.getType() == 0 ? "管理员" : "普通角色")
                .build();
    }
}

package io.daff.uacs.service.entity.po;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * role琛ㄧ殑瀹炰綋绫籠n *
 * @author wangzhengjin
 * @since 2021/05/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private String id;

    private String name;

    private String desc;

    private Byte type;

    private Long createBy;

    private Long updateBy;

    private Date createAt;

    private Date updateAt;

    private String detail;
}
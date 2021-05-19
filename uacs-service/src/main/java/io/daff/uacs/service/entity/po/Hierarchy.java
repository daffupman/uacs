package io.daff.uacs.service.entity.po;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * hierarchy琛ㄧ殑瀹炰綋绫籠n *
 * @author wangzhengjin
 * @since 2021/05/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hierarchy {
    private String id;

    private String name;

    private String desc;

    private Integer seq;

    private Integer parentId;

    private Boolean deleted;

    private String domainId;

    private Date createAt;

    private Date updateAt;

    private Long createBy;

    private Long updateBy;

    private String detail;
}
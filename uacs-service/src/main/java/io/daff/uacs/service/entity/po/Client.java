package io.daff.uacs.service.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * app_info表的实体类
 *
 * @author wangzhengjin
 * @since 2021/05/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Integer id;

    private String appId;

    private String appSecret;

    private Integer isDel;
}
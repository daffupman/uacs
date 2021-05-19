package io.daff.uacs.service.entity.resp.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页对象模型
 *
 * @author daff
 * @since 2021/5/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {

    /**
     * 数据的总数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> list;

    public static <T> Page<T> of(Long total, List<T> list) {
        return new Page<>(total, list);
    }

}

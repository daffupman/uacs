package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Hierarchy;

import java.util.List;

public interface HierarchyMapper {
    int batchInsert(List<Hierarchy> records);

    int deleteByIds(List<String> ids);

    List<Hierarchy> select(Hierarchy record);

    List<Hierarchy> selectByIds(List<String> ids);

    int batchUpdate(List<Hierarchy> records);
}
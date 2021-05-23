package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Hierarchy;

import java.util.List;

public interface HierarchyMapper {
    int insert(Hierarchy record);

    int batchInsert(List<Hierarchy> records);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int update(Hierarchy record);

    int batchUpdate(List<Hierarchy> records);

    List<Hierarchy> select(Hierarchy record);

    Hierarchy selectOne(Hierarchy record);

    List<Hierarchy> selectById(String id);

    List<Hierarchy> selectByIds(List<String> ids);

    List<Hierarchy> selectAll();

    List<Integer> selectHierarchyListByParentId(String topHierarchy);

    List<String> listByIdentity(List<Integer> limitHierarchyList);
}
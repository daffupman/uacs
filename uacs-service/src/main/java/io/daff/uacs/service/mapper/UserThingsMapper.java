package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.UserThings;

import java.util.List;

public interface UserThingsMapper {
    int insert(UserThings record);

    int batchInsert(List<UserThings> records);

    int deleteById(Long id);

    int deleteByIds(List<Long> ids);

    int update(UserThings record);

    int batchUpdate(List<UserThings> records);

    List<UserThings> select(UserThings record);

    UserThings selectOne(UserThings record);

    List<UserThings> selectById(Long id);

    List<UserThings> selectByIds(List<Long> ids);

    List<UserThings> selectAll();
}
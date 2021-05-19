package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.UserThings;

import java.util.List;

public interface UserThingsMapper {
    int batchInsert(List<UserThings> records);

    int deleteByIds(List<Long> ids);

    List<UserThings> select(UserThings record);

    List<UserThings> selectByIds(List<Long> ids);

    int batchUpdate(List<UserThings> records);
}
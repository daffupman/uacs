package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Role;

import java.util.List;

public interface RoleMapper {
    int batchInsert(List<Role> records);

    int deleteByIds(List<String> ids);

    List<Role> select(Role record);

    List<Role> selectByIds(List<String> ids);

    int batchUpdate(List<Role> records);
}
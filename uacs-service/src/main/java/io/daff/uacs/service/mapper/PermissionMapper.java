package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Permission;

import java.util.List;

public interface PermissionMapper {
    int batchInsert(List<Permission> records);

    int deleteByIds(List<String> ids);

    List<Permission> select(Permission record);

    List<Permission> selectByIds(List<String> ids);

    int batchUpdate(List<Permission> records);
}
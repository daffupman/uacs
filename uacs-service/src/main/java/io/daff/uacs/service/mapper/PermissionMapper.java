package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {
    int insert(Permission record);

    int batchInsert(List<Permission> records);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int update(Permission record);

    int batchUpdate(List<Permission> records);

    List<Permission> select(Permission record);

    Permission selectOne(Permission record);

    List<Permission> selectById(String id);

    List<Permission> selectByIds(List<String> ids);

    List<Permission> selectAll();

    List<Permission> selectByRoleIds(@Param("roleIdList") List<String> roleIdList);
}
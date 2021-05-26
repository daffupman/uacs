package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    int insert(Role record);

    int batchInsert(List<Role> records);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int update(Role record);

    int batchUpdate(List<Role> records);

    List<Role> select(Role record);

    Role selectOne(Role record);

    Role selectById(String id);

    List<Role> selectByIds(List<String> ids);

    List<Role> selectAll();

    List<Role> selectByUserId(Long userId);

    List<String> selectRolesByUserId(Long curUserSsoId);

    List<Role> selectByConditions(@Param("name") String name,
                                  @Param("underRoleIds") List<String> underRoleIds);
}
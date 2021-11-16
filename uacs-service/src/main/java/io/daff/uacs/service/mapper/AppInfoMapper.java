package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.AppInfo;

import java.util.List;

public interface AppInfoMapper {
    int insert(AppInfo record);

    int batchInsert(List<AppInfo> records);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int update(AppInfo record);

    int batchUpdate(List<AppInfo> records);

    List<AppInfo> select(AppInfo record);

    AppInfo selectOne(AppInfo record);

    List<AppInfo> selectById(String id);

    List<AppInfo> selectByIds(List<String> ids);

    List<AppInfo> selectAll();

    List<AppInfo> selectByUserId(Long userId);
}
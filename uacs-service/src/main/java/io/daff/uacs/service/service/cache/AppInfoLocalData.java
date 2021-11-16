package io.daff.uacs.service.service.cache;

import io.daff.uacs.service.entity.po.AppInfo;
import io.daff.uacs.service.mapper.AppInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2021/11/15
 */
@Component
@Slf4j
public class AppInfoLocalData implements Loader{

    @Resource
    private AppInfoMapper appInfoMapper;

    private Map<String, AppInfo> appInfoCache = new ConcurrentHashMap<>();

    public String getAppSecretById(String appId) {
        return appInfoCache.get(appId) == null ? null : appInfoCache.get(appId).getAppSecret();
    }

    @Override
    public void load() {
        new Thread(() -> {
            while (true) {
                List<AppInfo> appInfoList = appInfoMapper.selectAll();
                appInfoCache = appInfoList.stream().collect(Collectors.toMap(AppInfo::getAppId, appInfo -> appInfo));
                log.info("local data => app info load success!");

                try {
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "app-info-loader").start();
    }

}

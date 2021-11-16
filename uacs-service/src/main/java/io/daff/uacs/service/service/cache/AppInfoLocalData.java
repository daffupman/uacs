package io.daff.uacs.service.service.cache;

import io.daff.uacs.service.entity.po.AppInfo;
import io.daff.uacs.service.entity.po.Client;
import io.daff.uacs.service.mapper.ClientMapper;
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
    private ClientMapper clientMapper;

    private Map<String, Client> clientCache = new ConcurrentHashMap<>();

    public String getAppSecretById(String appId) {
        return clientCache.get(appId) == null ? null : clientCache.get(appId).getAppSecret();
    }

    @Override
    public void load() {
        new Thread(() -> {
            while (true) {
                List<Client> clientList = clientMapper.selectAll();
                clientCache = clientList.stream().collect(Collectors.toMap(Client::getAppId, client -> client));
                log.info("local data => client load success!");

                try {
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "client-loader").start();
    }

}

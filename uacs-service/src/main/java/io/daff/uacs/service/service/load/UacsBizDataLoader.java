package io.daff.uacs.service.service.load;

import io.daff.cache.BizDataLoader;
import io.daff.logging.DaffLogger;
import io.daff.logging.module.InnerModule;
import io.daff.uacs.service.entity.po.Client;
import io.daff.uacs.service.mapper.ClientMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author daff
 * @since 2021/11/15
 */
@Component
public class UacsBizDataLoader implements BizDataLoader {

    private static final DaffLogger log = DaffLogger.getLogger(UacsBizDataLoader.class);

    @Resource
    private ClientMapper clientMapper;

    private Map<String, Client> clientCache = new ConcurrentHashMap<>();
    private boolean clientLoadFinished;

    public String getAppSecretById(String appId) {
        return clientCache.get(appId) == null ? null : clientCache.get(appId).getAppSecret();
    }

    @Override
    public void load() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());
        ses.scheduleAtFixedRate(() -> {
            List<Client> clientList = clientMapper.selectAll();
            clientCache = clientList.stream().collect(Collectors.toMap(Client::getAppId, client -> client));
            log.info("local data => client load success!", InnerModule.CACHE);
            clientLoadFinished = true;
        }, 0L, 10, TimeUnit.MINUTES);
    }

    @Override
    public boolean finish() {
        return clientLoadFinished;
    }

}

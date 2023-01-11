package io.daff.uacs.web.interceptor.sign.secret;

import io.daff.uacs.service.service.cache.UacsBizDataLoader;
import io.daff.utils.common.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 从数据库中获取密钥
 *
 * @author daff
 * @since 2023/1/6
 */
@Component
public class DatabaseSecretStorage implements SecretStorage {

    @Resource
    private UacsBizDataLoader uacsBizDataLoader;

    @Override
    public String getSecretByAppId(String appId) {
        return StringUtil.hasText(appId) ? uacsBizDataLoader.getAppSecretById(appId) : null;
    }
}

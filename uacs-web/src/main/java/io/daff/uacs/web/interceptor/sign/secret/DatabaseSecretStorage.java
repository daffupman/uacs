package io.daff.uacs.web.interceptor.sign.secret;

import io.daff.uacs.service.service.load.UacsBizDataLoader;
import io.daff.utils.common.StringUtil;
import io.daff.web.enums.Hint;
import io.daff.web.exception.BaseException;
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
        if (!uacsBizDataLoader.finish()) {
            throw new BaseException(Hint.DATA_UNAVAILABLE, "数据还未加载完成，请稍后重试");
        }
        return StringUtil.hasText(appId) ? uacsBizDataLoader.getAppSecretById(appId) : null;
    }
}

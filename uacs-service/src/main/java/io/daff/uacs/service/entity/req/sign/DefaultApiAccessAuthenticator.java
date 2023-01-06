package io.daff.uacs.service.entity.req.sign;

import io.daff.uacs.service.entity.req.sign.secret.SecretStorage;
import io.daff.web.exception.ParamValidateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author daff
 * @since 2023/1/6
 */
@Component
public class DefaultApiAccessAuthenticator implements ApiAccessAuthenticator {

    private final SecretStorage secretStorage;

    public DefaultApiAccessAuthenticator(SecretStorage secretStorage) {
        this.secretStorage = secretStorage;
    }

    @Override
    public void auth(HttpServletRequest request, Map<String, Object> params) {
        // 生成签名对象
        Signature signature = new Signature().build(request);

        // 调试模式
        if (signature.isDebug()) {
            return;
        }

        // 重放攻击验证
        if (signature.replay()) {
            throw new ParamValidateException("接口调用已过期");
        }

        String secret = secretStorage.getSecretByAppId(signature.getAppId());
        if (secret == null) {
            throw new ParamValidateException("无效的app_id");
        }

        if (!signature.verify(params, secret)) {
            throw new ParamValidateException("签名验证错误");
        }
    }
}

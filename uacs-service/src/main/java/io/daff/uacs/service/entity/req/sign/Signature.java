package io.daff.uacs.service.entity.req.sign;

import io.daff.uacs.service.util.Md5Util;
import io.daff.web.exception.ParamValidateException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 需要验签的请求模型
 *
 * @author daff
 * @since 2021/11/15
 */
public class Signature {

    private String appId;
    private Long timestamp;
    private String signature;
    private Boolean debug;

    private static final String HEADER_APP_ID = "app_id";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_SIGNATURE = "signature";
    private static final String HEADER_DEBUG = "debug";
    private static final Integer DEFAULT_CALL_INTERVAL = 1000 * 60 * 500;

    public Signature build(HttpServletRequest request) {
        if (request == null) {
            throw new ParamValidateException("request not null");
        }

        this.appId = request.getHeader(HEADER_APP_ID);
        if (StringUtils.isEmpty(appId)) {
            throw new ParamValidateException("app_id is null");
        }

        String timestampStr = request.getHeader(HEADER_TIMESTAMP);
        if (StringUtils.isEmpty(timestampStr)) {
            throw new ParamValidateException("timestamp is null");
        }
        try {
            this.timestamp = Long.valueOf(timestampStr);
        } catch (NumberFormatException e) {
            throw new ParamValidateException("timestamp is not a number");
        }

        this.signature = request.getHeader(HEADER_SIGNATURE);
        if (StringUtils.isEmpty(signature)) {
            throw new ParamValidateException("signature is null");
        }

        this.debug = Boolean.valueOf(request.getHeader(HEADER_DEBUG));

        return this;
    }

    /**
     * 验签
     */
    public boolean verify(Map<String, Object> params, String secret) {
        params = sort(params);
        params.put(HEADER_APP_ID, this.appId);
        params.put(HEADER_TIMESTAMP, this.timestamp);
        String signature = sign(params, secret);
        return Objects.equals(this.signature, signature);
    }

    /**
     * 验证重放攻击
     */
    public boolean replay() {
        return System.currentTimeMillis() - timestamp > DEFAULT_CALL_INTERVAL;
    }

    public String getAppId() {
        return appId;
    }

    public Boolean isDebug() {
        return debug;
    }

    /**
     * 签名规则：Md5({k1=v1#k2=v2}secret)
     *
     * @param params 接口入参
     * @param secret 密钥
     * @return 签名串
     */
    private String sign(Map<String, Object> params, String secret) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("#");
        }
        sb.replace(sb.length() - 1, sb.length() - 1, "}");
        sb.append(secret);
        return Md5Util.encode(sb.toString());
    }

    /**
     * 参数排序
     */
    private Map<String, Object> sort(Map<String, Object> params) {
        TreeMap<String, Object> map = new TreeMap<>(Comparator.naturalOrder());
        map.putAll(params);
        return map;
    }
}

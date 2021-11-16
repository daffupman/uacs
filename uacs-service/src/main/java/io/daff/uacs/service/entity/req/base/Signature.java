package io.daff.uacs.service.entity.req.base;

import io.daff.exception.ParamValidateException;
import io.daff.uacs.service.util.Md5Util;
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

    public boolean verify(Map<String, Object> params, String secret) {
        params = sort(params);
        params.put(HEADER_APP_ID, this.appId);
        params.put(HEADER_TIMESTAMP, this.timestamp);
        String signature = sign(params, secret);
        return Objects.equals(this.signature, signature);
    }

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

    public String getAppId() {
        return appId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Boolean isDebug() {
        return debug;
    }
}

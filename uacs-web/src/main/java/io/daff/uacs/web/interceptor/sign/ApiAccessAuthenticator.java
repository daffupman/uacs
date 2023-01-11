package io.daff.uacs.web.interceptor.sign;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author daff
 * @since 2023/1/6
 */
public interface ApiAccessAuthenticator {

    void auth(HttpServletRequest request, Map<String, Object> params);
}

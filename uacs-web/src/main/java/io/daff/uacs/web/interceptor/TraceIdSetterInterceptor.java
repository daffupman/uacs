package io.daff.uacs.web.interceptor;

import io.daff.uacs.core.consts.AttrNames;
import io.daff.uacs.core.util.IdUtil;
import io.daff.utils.common.StringUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设置日志的traceId
 *
 * @author daff
 * @since 2023/1/11
 */
@Component
public class TraceIdSetterInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private IdUtil idUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(AttrNames.TRACE_ID);
        if (!StringUtil.hasText(traceId)) {
            traceId = idUtil.nextId().toString();
        }
        // 设置MDC
        MDC.put(AttrNames.TRACE_ID, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(AttrNames.TRACE_ID);
    }
}

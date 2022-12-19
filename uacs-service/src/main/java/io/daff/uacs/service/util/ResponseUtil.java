package io.daff.uacs.service.util;

import com.google.common.base.Charsets;
import io.daff.logging.DaffLogger;
import io.daff.logging.module.InnerModule;
import io.daff.web.entity.Response;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;

/**
 * Response工具类
 *
 * @author daffupman
 * @since 2020/7/12
 */
public class ResponseUtil {

    private static final DaffLogger log = DaffLogger.getLogger(ResponseUtil.class);

    /**
     * 向response中输出异常信息
     */
    public static <T> void printJsonError(ServletResponse response, Response<T> body) {
        response.setCharacterEncoding(Charsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        try {
            String errorMsg = JacksonUtil.JacksonConfig.getInstance().writeValueAsString(body);
            response.getWriter().write(errorMsg);
        } catch (Exception e) {
            log.error("写异常到response失败", InnerModule.WEB, e);
        }
    }

}

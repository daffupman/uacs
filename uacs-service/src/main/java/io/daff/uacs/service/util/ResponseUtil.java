package io.daff.uacs.service.util;

import com.google.common.base.Charsets;
import io.daff.web.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;

/**
 * Response工具类
 *
 * @author daffupman
 * @since 2020/7/12
 */
@Slf4j
public class ResponseUtil {

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
            log.error("写异常到response失败", e);
        }
    }

}

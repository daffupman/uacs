package io.daff.uacs.cms.config.shiro;

import io.daff.logging.DaffLogger;
import io.daff.uacs.cms.config.shiro.token.JwtToken;
import io.daff.uacs.core.enums.BaseModule;
import io.daff.uacs.service.entity.dto.OAuthExtraInfo;
import io.daff.uacs.service.util.JwtUtil;
import io.daff.uacs.service.util.ResponseUtil;
import io.daff.uacs.service.util.SimpleRedisUtil;
import io.daff.web.consts.GlobalConstants;
import io.daff.web.entity.Response;
import io.daff.web.enums.Hint;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * jwt认证过滤器
 *
 * @author daff
 * @since 2020/7/12
 */
@Component
public class StatelessAuthcFilter extends BasicHttpAuthenticationFilter {

    private static final DaffLogger log = DaffLogger.getLogger(StatelessAuthcFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_REDIS_PREFIX = "access_token:";
    private final SimpleRedisUtil simpleRedisUtil;

    public StatelessAuthcFilter(SimpleRedisUtil simpleRedisUtil) {
        this.simpleRedisUtil = simpleRedisUtil;
    }

    /**
     * 从header尝试获取token，如果获取到则去登录
     *
     * @param request     请求
     * @param response    响应
     * @param mappedValue mappedValue
     * @return 访问通过
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        String accessToken = req.getHeader(GlobalConstants.AUTHORIZATION);
        if (StringUtils.isEmpty(accessToken)) {
            log.error("使用token登录失败，header缺少access_token参数", BaseModule.AUTHC);
            Response<Void> error = Response.error(Hint.PARAM_MISS_ERROR, "缺失访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }

        // 尝试解析accessToken，如果解析失败，代表是一个错误的token
        if (!accessToken.startsWith(BEARER_PREFIX)) {
            log.error("非法的访问令牌", BaseModule.AUTHC);
            Response<Void> error = Response.error(Hint.PARAM_VALIDATE_ERROR, "非法的访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }
        accessToken = accessToken.substring(BEARER_PREFIX.length());
        String subjectId;
        try {
            subjectId = JwtUtil.getSubjectId(accessToken);
            if (StringUtils.isEmpty(subjectId)) {
                log.error("非法的访问令牌，令牌无subjectId: {}", BaseModule.AUTHC, accessToken);
                Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, "非法的访问令牌");
                ResponseUtil.printJsonError(response, error);
                return false;
            }
        } catch (Exception e) {
            log.error("非法的访问令牌", BaseModule.AUTHC, e);
            Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, "非法的访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }

        if (JwtUtil.isExpired(accessToken) || StringUtils.isEmpty(simpleRedisUtil.get(ACCESS_TOKEN_REDIS_PREFIX + subjectId))) {
            log.error("访问令牌已过期", BaseModule.AUTHC);
            Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, "过期的访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }

        OAuthExtraInfo oAuthExtraInfo = JwtUtil.getOAuthExtraInfo(accessToken);
        if (oAuthExtraInfo == null) {
            log.error("错误的accessToken，该token中没有OAuthExtraInfo信息：{}", BaseModule.AUTHC, accessToken);
            Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, "非法的访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }
        if (!GlobalConstants.ACCESS_TOKEN.equalsIgnoreCase(oAuthExtraInfo.getTokenType())) {
            Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, "非法的访问令牌");
            ResponseUtil.printJsonError(response, error);
            return false;
        }

        // 尝试使用accessToken登录
        try {
            getSubject(request, response).login(new JwtToken(accessToken));
        } catch (AuthenticationException e) {
            log.error("使用token登录失败", BaseModule.AUTHC, e);
            Response<Void> error = Response.error(Hint.AUTHENTICATION_FAILED, e.getMessage());
            ResponseUtil.printJsonError(response, error);
            // 登录失败
            return false;
        }

        request.setAttribute(GlobalConstants.CURRENT_LOGIN_USER, JwtUtil.getSubjectId(accessToken));

        // 登录成功
        return true;
    }

}

package io.daff.uacs.cms.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

/**
 * 封装shiro的session操作的工具类
 *
 * @author daff
 * @since 2020/8/13
 */
public class ShiroSessionUtils {

    /**
     * 获取shiro中的session
     */
    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 往session中存储kv
     */
    public static void setAttribute(Object key, Object value) {
        SecurityUtils.getSubject().getSession().setAttribute(key, value);
    }

    /**
     * 从session中获取value
     */
    public static Object getAttribute(Object key) {
        return SecurityUtils.getSubject().getSession().getAttribute(key);
    }

    /**
     * 按key移除session中的value
     */
    public static void removeAttribute(Object key) {
        SecurityUtils.getSubject().getSession().removeAttribute(key);
    }
}

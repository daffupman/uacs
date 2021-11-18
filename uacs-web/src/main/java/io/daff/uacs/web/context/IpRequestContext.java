package io.daff.uacs.web.context;

import java.io.Serializable;

/**
 * @author daffupman
 * @since 2021/4/2
 */
public class IpRequestContext implements Serializable {

    private static final ThreadLocal<String> ipThreadLocal = new ThreadLocal<>();

    public static String getIp() {
        return ipThreadLocal.get();
    }

    public static void set(String ip) {
        ipThreadLocal.set(ip);
    }

    public static void remove() {
        ipThreadLocal.remove();
    }
}

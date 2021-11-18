package io.daff.uacs.web.context;

import io.daff.entity.Response;

import java.io.Serializable;

/**
 * @author daffupman
 * @since 2021/4/2
 */
@SuppressWarnings("rawtypes")
public class ApiBodyContext implements Serializable {

    private static final ThreadLocal<Response> apiBodyThreadLocal = new ThreadLocal<>();

    public static Response get() {
        return apiBodyThreadLocal.get();
    }

    public static void set(Response apiBody) {
        apiBodyThreadLocal.set(apiBody);
    }

    public static void remove() {
        apiBodyThreadLocal.remove();
    }
}

package io.daff.uacs.core.enums;

import io.daff.logging.module.Module;

/**
 * 定义系统级别的日志模块
 *
 * @author daff
 * @since 2022/12/19
 */
public enum BaseModule implements Module {

    MONITOR(20, "DAFF.MONITOR"),
    AUTHC(21, "DAFF.AUTHC"),
    PAGE(22, "DAFF.PAGE"),
    ERROR(23, "DAFF.ERROR"),
    THREAD(24, "DAFF.THREAD")
    ;

    private final Integer no;
    private final String code;

    BaseModule(Integer no, String code) {
        this.no = no;
        this.code = code;
    }

    @Override
    public Integer no() {
        return this.no;
    }

    @Override
    public String code() {
        return this.code;
    }
}

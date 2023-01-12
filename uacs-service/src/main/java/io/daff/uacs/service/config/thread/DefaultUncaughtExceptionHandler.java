package io.daff.uacs.service.config.thread;

import io.daff.logging.DaffLogger;
import io.daff.uacs.core.enums.BaseModule;

/**
 * 线程的统一异常处理器
 *
 * @author daff
 * @since 2023/1/11
 */
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final DaffLogger logger = DaffLogger.getLogger(DefaultUncaughtExceptionHandler.class);

    /**
     * 日志记录
     * @param t 线程
     * @param e 异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("子线程异常信息捕获", BaseModule.THREAD, e);
    }
}

package io.daff.uacs.service.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.daff.uacs.service.config.thread.DefaultUncaughtExceptionHandler;
import io.daff.uacs.service.config.thread.ThreadPoolExecutorWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @author daff
 * @since 2023/1/11
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutorWrapper threadPoolExecutor() {
        return new ThreadPoolExecutorWrapper(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                new ThreadFactoryBuilder().setNameFormat("uacs-%d").setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler()).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}

package io.daff.uacs.web;

import io.daff.cache.BizDataLoader;
import io.daff.cache.PreCacheDataExecutor;
import io.daff.logging.DaffLogger;
import io.daff.logging.module.InnerModule;
import io.daff.uacs.service.config.thread.DefaultUncaughtExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * @author daff
 * @since 2021/5/22
 */
@SpringBootApplication
@MapperScan(basePackages = {"io.daff.uacs.service.mapper"})
@ComponentScan("io.daff.uacs")
public class UacsWebApplication implements CommandLineRunner {

    private static final DaffLogger logger = DaffLogger.getLogger(UacsWebApplication.class);

    @Resource
    private BizDataLoader bizDataLoader;

    public static void main(String[] args) {
        SpringApplication.run(UacsWebApplication.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        // 设置线程的全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());

        // 数据加载
        if (bizDataLoader != null) {
            new PreCacheDataExecutor(bizDataLoader).exec();
            logger.info("uacs cache data load success!", InnerModule.CACHE);
        }
    }
}

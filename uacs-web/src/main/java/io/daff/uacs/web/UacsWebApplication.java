package io.daff.uacs.web;

import io.daff.uacs.service.config.thread.DefaultUncaughtExceptionHandler;
import io.daff.uacs.service.service.cache.BizDataLoader;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author daff
 * @since 2021/5/22
 */
@SpringBootApplication
@MapperScan(basePackages = {"io.daff.uacs.service.mapper"})
@ComponentScan("io.daff.uacs")
public class UacsWebApplication implements CommandLineRunner {

    @Resource
    private List<BizDataLoader> bizDataLoaders;

    public static void main(String[] args) {
        SpringApplication.run(UacsWebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 数据加载
        if (!CollectionUtils.isEmpty(bizDataLoaders)) {
            bizDataLoaders.forEach(BizDataLoader::load);
        }

        // 设置线程的全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
    }
}

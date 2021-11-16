package io.daff.uacs.web;

import io.daff.uacs.service.service.cache.Loader;
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
    private List<Loader> loaders;

    public static void main(String[] args) {
        SpringApplication.run(UacsWebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!CollectionUtils.isEmpty(loaders)) {
            loaders.forEach(Loader::load);
        }
    }
}

package io.daff.uacs.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author daff
 * @since 2021/5/22
 */
@SpringBootApplication
@MapperScan(basePackages = {"io.daff.uacs.service.mapper"})
@ComponentScan("io.daff.uacs")
public class UacsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(UacsWebApplication.class, args);
    }
}

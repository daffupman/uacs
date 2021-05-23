package io.daff.uacs.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"io.daff.uacs.service.mapper"})
@ComponentScan("io.daff.uacs")
public class UacsCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UacsCmsApplication.class, args);
    }
}

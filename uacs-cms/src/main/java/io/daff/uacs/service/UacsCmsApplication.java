package io.daff.uacs.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"io.daff.uacs.service.mapper"})
public class UacsCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UacsCmsApplication.class, args);
    }
}

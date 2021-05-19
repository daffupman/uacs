package io.daff.uacs.service;

import io.daff.uacs.service.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UacsCmsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private RoleMapper roleMapper;

}

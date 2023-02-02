package io.daff.uacs.web.debugspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daff
 * @since 2023/1/31
 */
@Component
public class A {

    @Autowired
    private B b;
}

package io.daff.uacs.core.util;

import io.daff.utils.common.SnowFlake;
import org.springframework.stereotype.Component;

/**
 * @author daff
 * @since 2021/5/20
 */
@Component
public class IdUtil {

    private final SnowFlake snowFlake = new SnowFlake();

    public Long nextId() {
        return snowFlake.nextId();
    }
}

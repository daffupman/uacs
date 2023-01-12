package io.daff.uacs.core.util;

import io.daff.utils.id.DistributeIdGenerator;
import io.daff.utils.id.LogTraceIdGenerator;
import io.daff.utils.id.RandomIdGenerator;
import io.daff.utils.id.SnowFlakeIdGenerator;
import org.springframework.stereotype.Component;

/**
 * @author daff
 * @since 2021/5/20
 */
@Component
public class IdUtil {

    private final LogTraceIdGenerator logTraceIdGenerator = new RandomIdGenerator();
    private final DistributeIdGenerator distributeIdGenerator = new SnowFlakeIdGenerator();

    public Long nextDistributeId() {
        return Long.valueOf(distributeIdGenerator.gen());
    }

    public String nextTraceId() {
        return logTraceIdGenerator.gen();
    }
}

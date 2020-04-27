package com.fanxuankai.zeus.common.data.redis.config;

import com.fanxuankai.zeus.common.data.redis.lock.DistributedLocker;
import org.springframework.context.annotation.Bean;

/**
 * @author fanxuankai
 */
public class LockAutoConfiguration {

    @Bean
    public LockMethodInterceptor lockMethodInterceptor(DistributedLocker distributedLocker) {
        return new LockMethodInterceptor(distributedLocker);
    }

    @Bean
    public LockPointcutAdvisor lockPointcutAdvisor(LockMethodInterceptor lockMethodInterceptor) {
        return new LockPointcutAdvisor(lockMethodInterceptor);
    }
}

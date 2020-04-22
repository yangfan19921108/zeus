package com.fanxuankai.zeus.common.data.redis.configuration;

import com.fanxuankai.zeus.common.data.redis.lock.DistributedLocker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@Configuration
public class EnableDistributedLockerConfiguration {

    @Resource
    private DistributedLocker distributedLocker;

    @Bean
    public LockPointcutAdvisor lockPointcutAdvisor() {
        return new LockPointcutAdvisor(distributedLocker);
    }
}

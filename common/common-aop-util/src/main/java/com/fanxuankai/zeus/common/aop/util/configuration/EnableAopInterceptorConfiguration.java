package com.fanxuankai.zeus.common.aop.util.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanxuankai
 */
@Configuration
public class EnableAopInterceptorConfiguration {

    @Bean
    public LogPointcutAdvisor logPointcutAdvisor() {
        return new LogPointcutAdvisor();
    }
}

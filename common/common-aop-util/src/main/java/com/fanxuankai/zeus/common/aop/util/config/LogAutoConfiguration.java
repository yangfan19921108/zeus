package com.fanxuankai.zeus.common.aop.util.config;

import org.springframework.context.annotation.Bean;

/**
 * @author fanxuankai
 */
public class LogAutoConfiguration {

    @Bean
    public LogMethodInterceptor logMethodInterceptor() {
        return new LogMethodInterceptor();
    }

    @Bean
    public LogPointcutAdvisor logPointcutAdvisor(LogMethodInterceptor logMethodInterceptor) {
        return new LogPointcutAdvisor(logMethodInterceptor);
    }
}

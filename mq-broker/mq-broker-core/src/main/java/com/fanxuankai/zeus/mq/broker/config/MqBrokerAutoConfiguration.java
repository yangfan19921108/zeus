package com.fanxuankai.zeus.mq.broker.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(MqBrokerProperties.class)
@MapperScan("com.fanxuankai.zeus.mq.broker.mapper")
@ComponentScan({"com.fanxuankai.zeus.mq.broker"})
@EnableTransactionManagement
@EnableScheduling
public class MqBrokerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor(MqBrokerProperties mqBrokerProperties) {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType(mqBrokerProperties.getDialectType());
        return page;
    }

}

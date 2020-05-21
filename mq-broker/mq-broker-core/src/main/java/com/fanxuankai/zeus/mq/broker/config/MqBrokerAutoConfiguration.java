package com.fanxuankai.zeus.mq.broker.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(MqBrokerProperties.class)
@MapperScan("com.fanxuankai.zeus.mq.broker.mapper")
@ComponentScan({"com.fanxuankai.zeus.mq.broker"})
@EnableTransactionManagement
public class MqBrokerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }

}

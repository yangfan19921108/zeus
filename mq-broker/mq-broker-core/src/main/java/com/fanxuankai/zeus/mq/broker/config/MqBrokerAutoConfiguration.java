package com.fanxuankai.zeus.mq.broker.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.fanxuankai.zeus.mq.broker.core.EventRegistry;
import com.fanxuankai.zeus.mq.broker.core.consume.EventListener;
import com.fanxuankai.zeus.mq.broker.core.consume.Listener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(MqBrokerProperties.class)
@MapperScan("com.fanxuankai.zeus.mq.broker.mapper")
@ComponentScan({"com.fanxuankai.zeus.mq.broker"})
@EnableTransactionManagement
@EnableScheduling
public class MqBrokerAutoConfiguration implements ApplicationContextAware {

    @Resource
    private MqBrokerProperties mqBrokerProperties;

    @PostConstruct
    public void init() {
        mqBrokerProperties.getEvents().forEach(EventRegistry::register);
    }

    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor(MqBrokerProperties mqBrokerProperties) {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType(mqBrokerProperties.getDialectType());
        return page;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(Listener.class).values()
                .forEach(o -> {
                    if (o instanceof EventListener) {
                        EventListener eventListener = (EventListener) o;
                        EventRegistry.addListener(eventListener.getClass().getAnnotation(Listener.class).event(),
                                eventListener);
                    }
                });
    }
}

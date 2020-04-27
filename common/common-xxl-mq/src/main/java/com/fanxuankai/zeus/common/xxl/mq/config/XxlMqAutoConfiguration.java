package com.fanxuankai.zeus.common.xxl.mq.config;

import com.xxl.mq.client.factory.impl.XxlMqSpringClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanxuankai
 */
@Configuration
@EnableConfigurationProperties(XxlMqConfiguration.class)
public class XxlMqAutoConfiguration {

    private final XxlMqConfiguration configuration;

    public XxlMqAutoConfiguration(XxlMqConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public XxlMqSpringClientFactory getXxlMqConsumer() {
        XxlMqSpringClientFactory xxlMqSpringClientFactory = new XxlMqSpringClientFactory();
        xxlMqSpringClientFactory.setAdminAddress(configuration.getAddress());
        return xxlMqSpringClientFactory;
    }
}

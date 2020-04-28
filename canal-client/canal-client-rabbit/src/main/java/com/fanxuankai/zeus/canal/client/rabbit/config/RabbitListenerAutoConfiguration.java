package com.fanxuankai.zeus.canal.client.rabbit.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@Import({RabbitListenerAutoConfigurationImportRegistrar.class})
public class RabbitListenerAutoConfiguration implements InitializingBean {

    @Resource
    private ConnectionFactory connectionFactory;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.afterPropertiesSet();
    }
}

package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author fanxuankai
 */
@Import({MqBrokerRabbitAutoConfigurationImportRegistrar.class})
public class MqBrokerRabbitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessageSendConsumer.class)
    public MessageSendConsumer messageSendConsumer() {
        return new RabbitMessageSendConsumer();
    }
}

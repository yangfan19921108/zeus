package com.fanxuankai.zeus.mq.broker.rabbit;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.consume.AbstractMqConsumer;
import com.fanxuankai.zeus.mq.broker.core.consume.EventListenerFactory;
import com.fanxuankai.zeus.mq.broker.core.consume.MqConsumer;
import com.fanxuankai.zeus.mq.broker.core.produce.AbstractMqProducer;
import com.fanxuankai.zeus.mq.broker.core.produce.MqProducer;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fanxuankai
 */
@Slf4j
@Import({MqBrokerRabbitAutoConfigurationImportRegistrar.class})
public class MqBrokerRabbitAutoConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageListenerContainer simpleMessageQueueLister(ConnectionFactory connectionFactory,
                                                             AbstractMqConsumer<Event> mqConsumer,
                                                             ThreadPoolExecutor threadPoolExecutor) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 监听的队列
        EventListenerFactory.getListeners().forEach(listener -> container.addQueueNames(listener.event()));
        // 是否重回队列
        container.setDefaultRequeueRejected(false);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) ->
                mqConsumer.accept(JSON.parseObject(new String(message.getBody()), Event.class)));
        return container;
    }

    @Bean
    @ConditionalOnMissingBean(MqProducer.class)
    public AbstractMqProducer messageSendConsumer(RabbitTemplate rabbitTemplate,
                                                  RabbitProperties rabbitProperties,
                                                  MsgSendService msgSendService) {
        String s = "___Begin___CorrelationDataIdSeparator___End___";
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            assert correlationData != null;
            String[] split = Objects.requireNonNull(correlationData.getId()).split(s);
            String topic = split[0];
            String code = split[1];
            if (ack) {
                msgSendService.success(topic, code);
            } else {
                msgSendService.failure(topic, code, Optional.ofNullable(cause).orElse("nack"));
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            Event event = JSON.parseObject(new String(message.getBody()), Event.class);
            String cause = "replyCode: " + replyCode + ", replyText: " + replyText + ", exchange: " + exchange;
            msgSendService.failure(routingKey, event.getKey(), cause);
        });
        return new AbstractMqProducer() {
            @Override
            public boolean isPublisherCallback() {
                return rabbitProperties.isPublisherConfirms() && rabbitProperties.isPublisherReturns();
            }

            @Override
            public void accept(Event event) {
                rabbitTemplate.convertAndSend(event.getName(), event,
                        new CorrelationData(event.getName() + s + event.getKey()));
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(MqConsumer.class)
    public AbstractMqConsumer<Event> mqConsumer() {
        return new AbstractMqConsumer<Event>() {
        };
    }

}

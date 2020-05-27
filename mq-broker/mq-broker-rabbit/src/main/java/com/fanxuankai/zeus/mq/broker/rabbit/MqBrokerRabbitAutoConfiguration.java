package com.fanxuankai.zeus.mq.broker.rabbit;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventRegistry;
import com.fanxuankai.zeus.mq.broker.core.consume.AbstractMqConsumer;
import com.fanxuankai.zeus.mq.broker.core.consume.MqConsumer;
import com.fanxuankai.zeus.mq.broker.core.produce.AbstractMqProducer;
import com.fanxuankai.zeus.mq.broker.core.produce.MqProducer;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Slf4j
public class MqBrokerRabbitAutoConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageListenerContainer simpleMessageQueueLister(ConnectionFactory connectionFactory,
                                                             AbstractMqConsumer<Event> mqConsumer,
                                                             AmqpAdmin amqpAdmin) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 监听的队列
        List<Queue> queues = EventRegistry.allEvent()
                .stream()
                .map(event -> new Queue(event, true))
                .collect(Collectors.toList());
        queues.forEach(queue -> {
            amqpAdmin.declareQueue(queue);
            container.addQueues(queue);
        });
        // 是否重回队列
        container.setDefaultRequeueRejected(false);
        // 手动签收
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setErrorHandler(throwable -> log.error("消费异常", throwable));
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            mqConsumer.accept(JSON.parseObject(new String(message.getBody()), Event.class));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        });
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

package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import org.springframework.amqp.core.AmqpTemplate;

import javax.annotation.Resource;

/**
 * RabbitMQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRabbitMqConsumer extends AbstractMqConsumer {

    @Resource
    protected AmqpTemplate amqpTemplate;

    @Override
    public void consume(MessageInfo messageInfo) {
        messageInfo.getMessages().forEach(s -> amqpTemplate.convertAndSend(messageInfo.getRoutingKey(), s));
    }

}

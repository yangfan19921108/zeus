package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import org.springframework.amqp.core.AmqpTemplate;

/**
 * RabbitMQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRabbitMqConsumer extends AbstractMqConsumer {

    private final AmqpTemplate amqpTemplate;

    public AbstractRabbitMqConsumer(ApplicationInfo applicationInfo) {
        super(applicationInfo);
        this.amqpTemplate = ApplicationContexts.getBean(AmqpTemplate.class);
    }

    @Override
    protected void onConsume(String routingKey, String data) {
        amqpTemplate.convertAndSend(routingKey, data);
    }
}

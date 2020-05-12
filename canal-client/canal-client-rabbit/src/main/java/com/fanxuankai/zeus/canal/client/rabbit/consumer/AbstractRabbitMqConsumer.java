package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRabbitMqConsumer extends AbstractMqConsumer {

    public AbstractRabbitMqConsumer(ApplicationInfo applicationInfo) {
        super(applicationInfo);
    }

    @Override
    protected void onConsume(String routingKey, String data) {
        ApplicationContexts.getBean(RabbitTemplate.class).convertAndSend(routingKey, data);
    }
}

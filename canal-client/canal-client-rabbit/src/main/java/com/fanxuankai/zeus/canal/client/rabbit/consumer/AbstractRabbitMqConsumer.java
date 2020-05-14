package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RabbitMQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRabbitMqConsumer extends AbstractMqConsumer {

    private final AmqpTemplate amqpTemplate;

    public AbstractRabbitMqConsumer(ApplicationInfo applicationInfo,
                                    RedisTemplate<Object, Object> redisTemplate,
                                    AmqpTemplate amqpTemplate) {
        super(applicationInfo, redisTemplate);
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    protected void onConsume(String routingKey, String data) {
        amqpTemplate.convertAndSend(routingKey, data);
    }
}

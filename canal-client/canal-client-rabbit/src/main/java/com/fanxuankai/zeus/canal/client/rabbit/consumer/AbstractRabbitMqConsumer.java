package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RabbitMQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRabbitMqConsumer extends AbstractMqConsumer {

    private final RabbitTemplate rabbitTemplate;

    public AbstractRabbitMqConsumer(ApplicationInfo applicationInfo,
                                    RedisTemplate<Object, Object> redisTemplate,
                                    RabbitTemplate rabbitTemplate) {
        super(applicationInfo, redisTemplate);
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected void onConsume(String routingKey, String data) {
        rabbitTemplate.convertAndSend(routingKey, data);
    }
}

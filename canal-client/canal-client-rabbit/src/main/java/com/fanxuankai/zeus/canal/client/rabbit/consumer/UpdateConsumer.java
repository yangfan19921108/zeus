package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.UpdateProcessable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 更新事件消费者
 *
 * @author fanxuankai
 */
public class UpdateConsumer extends AbstractRabbitMqConsumer implements UpdateProcessable {
    public UpdateConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate,
                          RabbitTemplate rabbitTemplate) {
        super(applicationInfo, redisTemplate, rabbitTemplate);
    }
}

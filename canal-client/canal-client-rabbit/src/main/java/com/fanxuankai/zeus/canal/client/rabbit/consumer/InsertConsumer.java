package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.InsertProcessable;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 新增事件消费者
 *
 * @author fanxuankai
 */
public class InsertConsumer extends AbstractRabbitMqConsumer implements InsertProcessable {
    public InsertConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate,
                          AmqpTemplate amqpTemplate) {
        super(applicationInfo, redisTemplate, amqpTemplate);
    }
}

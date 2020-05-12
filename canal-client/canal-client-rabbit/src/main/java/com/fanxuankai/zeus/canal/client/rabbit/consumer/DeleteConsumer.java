package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.DeleteProcessable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractRabbitMqConsumer implements DeleteProcessable {
    public DeleteConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate,
                          RabbitTemplate rabbitTemplate) {
        super(applicationInfo, redisTemplate, rabbitTemplate);
    }
}

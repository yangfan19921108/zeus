package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.InsertProcessable;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 新增事件消费者
 *
 * @author fanxuankai
 */
public class InsertConsumer extends AbstractXxlMqConsumer implements InsertProcessable {
    public InsertConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate) {
        super(applicationInfo, redisTemplate);
    }
}

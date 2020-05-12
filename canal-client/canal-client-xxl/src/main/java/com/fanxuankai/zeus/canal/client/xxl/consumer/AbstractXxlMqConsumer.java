package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * XXL-MQ 抽象消费者
 *
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractXxlMqConsumer extends AbstractMqConsumer {

    public AbstractXxlMqConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate) {
        super(applicationInfo, redisTemplate);
    }

    @Override
    protected void onConsume(String routingKey, String data) {
        XxlMqProducer.produce(new XxlMqMessage(routingKey, data));
    }

}

package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * XXL-MQ 抽象消费者
 *
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractXxlMqConsumer extends AbstractMqConsumer {

    private final ApplicationInfo applicationInfo;
    private final String consumeTag;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public AbstractXxlMqConsumer(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
        consumeTag = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                applicationInfo.uniqueString() + CommonConstants.SEPARATOR + RedisConstants.XXL_MQ_CONSUME);
    }

    @Override
    public void consume(MessageInfo messageInfo) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        // 考虑部分失败的情况, 需要做防重
        // 采用 MD5 消息摘要实现防重
        String key = consumeTag + CommonConstants.SEPARATOR + messageInfo.getRoutingKey();
        messageInfo.getMessages().forEach(message -> {
            Boolean absent = opsForHash.putIfAbsent(key, message.getMd5(), Boolean.TRUE);
            if (Objects.equals(absent, Boolean.TRUE)) {
                XxlMqProducer.produce(new XxlMqMessage(messageInfo.getRoutingKey(), message.getData()));
            } else {
                log.info("XXL-MQ 防重消费 {} {} {}", messageInfo.getRoutingKey(), message, applicationInfo.uniqueString());
            }
        });
        // 消费完成, 删除 key
        redisTemplate.delete(key);
    }

}

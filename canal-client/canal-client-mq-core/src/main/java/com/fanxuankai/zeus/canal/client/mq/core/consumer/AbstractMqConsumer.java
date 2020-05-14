package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

import static com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner.CONSUME_CONFIGURATION;

/**
 * MQ 抽象消费者
 *
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractMqConsumer implements MessageConsumer<MessageInfo> {

    private final ApplicationInfo applicationInfo;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final String consumeTag;

    public AbstractMqConsumer(ApplicationInfo applicationInfo, RedisTemplate<Object, Object> redisTemplate) {
        this.applicationInfo = applicationInfo;
        this.redisTemplate = redisTemplate;
        consumeTag = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                applicationInfo.uniqueString() + CommonConstants.SEPARATOR + RedisConstants.MQ_CONSUME);
    }

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        if (CONSUME_CONFIGURATION.getAnnotation(entryWrapper) == null) {
            return false;
        }
        CanalToMqMetadata metadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        return metadata == null || metadata.getEventTypes().contains(entryWrapper.getEventType());
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return Objects.requireNonNull(CONSUME_CONFIGURATION.getMetadata(entryWrapper)).getFilterMetadata();
    }

    @Override
    public void consume(MessageInfo messageInfo) {
        if (messageInfo.getMessages().size() > 1
                && !Objects.requireNonNull(CONSUME_CONFIGURATION.getMetadata(messageInfo.getRaw())).isRepeatableConsumption()) {
            HashOperations<Object, Object, Object> opsForHash = redisTemplate.opsForHash();
            // 考虑部分失败的情况, 需要做防重
            // 采用 MD5 消息摘要实现防重
            String key = consumeTag + CommonConstants.SEPARATOR + messageInfo.getRoutingKey();
            messageInfo.getMessages().forEach(message -> {
                Boolean absent = opsForHash.putIfAbsent(key, message.getMd5(), Boolean.TRUE);
                if (Objects.equals(absent, Boolean.TRUE)) {
                    onConsume(messageInfo.getRoutingKey(), message.getData());
                } else {
                    log.info("MQ 防重消费 {} {} {}", messageInfo.getRoutingKey(), message, applicationInfo.uniqueString());
                }
            });
            // 消费完成, 删除 key
            redisTemplate.delete(key);
        } else {
            messageInfo.getMessages().forEach(message -> onConsume(messageInfo.getRoutingKey(), message.getData()));
        }
    }

    /**
     * on consume
     *
     * @param routingKey topic
     * @param data       content
     */
    protected abstract void onConsume(String routingKey, String data);

}

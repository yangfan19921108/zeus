package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import org.springframework.data.redis.core.RedisTemplate;

import static com.fanxuankai.zeus.canal.client.redis.config.CanalToRedisScanner.CONSUME_CONFIGURATION;

/**
 * Redis 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRedisConsumer<R> implements MessageConsumer<R> {

    protected final RedisTemplate<Object, Object> redisTemplate;

    public AbstractRedisConsumer(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getAnnotation(entryWrapper) != null;
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return getMetadata(entryWrapper).getFilterMetadata();
    }

    protected CanalToRedisMetadata getMetadata(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        assert canalToRedisMetadata != null;
        return canalToRedisMetadata;
    }

}

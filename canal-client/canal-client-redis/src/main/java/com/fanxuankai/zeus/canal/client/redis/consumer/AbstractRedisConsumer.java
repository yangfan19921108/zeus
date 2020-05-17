package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import com.fanxuankai.zeus.data.redis.ObjectRedisTemplate;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;

import static com.fanxuankai.zeus.canal.client.redis.config.CanalToRedisScanner.CONSUME_CONFIGURATION;

/**
 * Redis 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRedisConsumer<R> implements MessageConsumer<R> {

    protected final ObjectRedisTemplate redisTemplate;

    public AbstractRedisConsumer() {
        this.redisTemplate = ApplicationContexts.getBean(ObjectRedisTemplate.class);
    }

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getAnnotation(entryWrapper) != null;
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return getMetadata(entryWrapper).getFilterMetadata();
    }

    @Override
    public Class<?> domainClass(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getDomain(entryWrapper);
    }

    protected CanalToRedisMetadata getMetadata(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        assert canalToRedisMetadata != null;
        return canalToRedisMetadata;
    }

}

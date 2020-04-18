package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import static com.fanxuankai.zeus.canal.client.redis.configuration.RedisRepositoryScanner.INTERFACE_BEAN_SCANNER;

/**
 * Redis 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractRedisConsumer<R> implements MessageConsumer<R> {

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        return INTERFACE_BEAN_SCANNER.getInterfaceBeanClass(entryWrapper) != null;
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper).getFilterMetadata();
    }

}

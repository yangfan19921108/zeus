package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.util.RedisKeyGenerator;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;

/**
 * 删表事件消费者
 *
 * @author fanxuankai
 */
public class EraseConsumer extends AbstractRedisConsumer<Set<Object>> {

    @Override
    public Set<Object> process(EntryWrapper entryWrapper) {
        Set<Object> keys = redisTemplate.keys(RedisKeyGenerator.keyOf(entryWrapper) + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }
        return keys;
    }

    @Override
    public boolean filterable() {
        return false;
    }

    @Override
    public void consume(Set<Object> keys) {
        redisTemplate.delete(keys);
    }
}

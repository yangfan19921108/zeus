package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import static com.fanxuankai.zeus.canal.client.redis.config.RedisRepositoryScanner.INTERFACE_BEAN_SCANNER;

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

    protected String keyOf(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata = INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        if (StringUtils.isNotBlank(canalToRedisMetadata.getKey())) {
            return canalToRedisMetadata.getKey();
        }
        return RedisUtils.key(entryWrapper.getSchemaName(), entryWrapper.getTableName());
    }

    protected String keyOf(EntryWrapper entryWrapper, String suffix) {
        CanalToRedisMetadata canalToRedisMetadata = INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        if (StringUtils.isNotBlank(canalToRedisMetadata.getKey())) {
            return RedisUtils.customKey(canalToRedisMetadata.getKey(), suffix);
        }
        return RedisUtils.key(entryWrapper.getSchemaName(), entryWrapper.getTableName(), suffix);
    }

}

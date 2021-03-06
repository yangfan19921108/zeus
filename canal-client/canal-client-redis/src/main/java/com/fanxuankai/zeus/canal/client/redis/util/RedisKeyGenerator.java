package com.fanxuankai.zeus.canal.client.redis.util;

import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import org.apache.commons.lang3.StringUtils;

import static com.fanxuankai.zeus.canal.client.redis.config.CanalToRedisScanner.CONSUME_CONFIGURATION;

/**
 * @author fanxuankai
 */
public class RedisKeyGenerator {

    /**
     * key
     *
     * @param entryWrapper 数据
     * @return prefix.schema.table
     */
    public static String keyOf(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        if (canalToRedisMetadata != null && StringUtils.isNotBlank(canalToRedisMetadata.getKey())) {
            return canalToRedisMetadata.getKey();
        }
        return RedisUtils.key(entryWrapper.getSchemaName(), entryWrapper.getTableName());
    }

    /**
     * key
     *
     * @param entryWrapper 数据
     * @param suffix       后缀
     * @return prefix.schema.table.suffix
     */
    public static String keyOf(EntryWrapper entryWrapper, String suffix) {
        CanalToRedisMetadata canalToRedisMetadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        if (canalToRedisMetadata != null && StringUtils.isNotBlank(canalToRedisMetadata.getKey())) {
            return RedisUtils.customKey(canalToRedisMetadata.getKey(), suffix);
        }
        return RedisUtils.key(entryWrapper.getSchemaName(), entryWrapper.getTableName(), suffix);
    }
}

package com.fanxuankai.zeus.canal.client.redis.config;

import com.fanxuankai.zeus.canal.client.core.config.ConsumeConfiguration;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;

/**
 * CanalToRedis 扫描器
 *
 * @author fanxuankai
 */
public class CanalToRedisScanner {

    public static final ConsumeConfiguration<CanalToRedis, CanalToRedisMetadata> CONSUME_CONFIGURATION
            = new ConsumeConfiguration<>(CanalToRedis.class, CanalToRedisMetadata::new);

}

package com.fanxuankai.zeus.canal.client.es.config;

import com.fanxuankai.zeus.canal.client.core.config.ConsumeConfiguration;
import com.fanxuankai.zeus.canal.client.es.annotation.CanalToEs;
import com.fanxuankai.zeus.canal.client.es.metadata.CanalToEsMetadata;

/**
 * CanalToRedis 扫描器
 *
 * @author fanxuankai
 */
public class CanalToEsScanner {

    public static final ConsumeConfiguration<CanalToEs, CanalToEsMetadata> CONSUME_CONFIGURATION
            = new ConsumeConfiguration<>(CanalToEs.class, CanalToEsMetadata::new);

}

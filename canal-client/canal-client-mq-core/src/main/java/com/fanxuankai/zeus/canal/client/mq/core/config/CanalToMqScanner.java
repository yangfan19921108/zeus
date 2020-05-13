package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.core.config.ConsumeConfiguration;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;

/**
 * CanalToMq 扫描器
 *
 * @author fanxuankai
 */
public class CanalToMqScanner {

    public static final ConsumeConfiguration<CanalToMq, CanalToMqMetadata> CONSUME_CONFIGURATION
            = new ConsumeConfiguration<>(CanalToMq.class, CanalToMqMetadata::new);

}

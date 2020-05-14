package com.fanxuankai.zeus.canal.client.core.flow;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author fanxuankai
 */
@Builder
@Getter
public class Config {
    private final ApplicationInfo applicationInfo;
    private final CanalProperties canalProperties;
    private final ConnectConfig connectConfig;
    @SuppressWarnings("rawtypes")
    private final Map<CanalEntry.EventType, MessageConsumer> consumerMap;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final boolean skip;
}
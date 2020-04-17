package com.fanxuankai.zeus.canal.client.core.model;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 消费者信息
 *
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class ConsumerInfo {
    /**
     * 消息消费者
     */
    @SuppressWarnings("rawtypes")
    private final Map<CanalEntry.EventType, MessageConsumer> consumerMap;
    /**
     * 应用程序信息
     */
    private final ApplicationInfo applicationInfo;
}
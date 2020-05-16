package com.fanxuankai.zeus.canal.client.mq.core.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.util.QueueNameUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import org.apache.commons.lang3.StringUtils;

import static com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner.CONSUME_CONFIGURATION;

/**
 * @author fanxuankai
 */
public class MqUtils {

    /**
     * 生成消息主题
     *
     * @param entryWrapper 数据
     * @param eventType    事件类型
     * @return prefix.schema.table.eventType
     */
    public static String routingKey(EntryWrapper entryWrapper, CanalEntry.EventType eventType) {
        CanalToMqMetadata metadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        if (metadata != null && StringUtils.isNotBlank(metadata.getName())) {
            return QueueNameUtils.customName(metadata.getName(), eventType);
        }
        Class<?> domainType = CONSUME_CONFIGURATION.getDomain(entryWrapper);
        CanalTableMetadata tableMetadata = CONSUME_CONFIGURATION.getCanalTableMetadata(domainType, true);
        return QueueNameUtils.name(tableMetadata.getSchema(), tableMetadata.getName(), eventType);
    }

}

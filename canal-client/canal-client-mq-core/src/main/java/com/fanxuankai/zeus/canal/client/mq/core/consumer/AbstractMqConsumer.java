package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.QueueNameUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner.INTERFACE_BEAN_SCANNER;

/**
 * MQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractMqConsumer implements MessageConsumer<MessageInfo> {

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        if (INTERFACE_BEAN_SCANNER.getInterfaceBeanClass(entryWrapper) == null) {
            return false;
        }
        CanalToMqMetadata metadata = INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        return metadata == null || metadata.getEventTypes().contains(entryWrapper.getEventType());
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper).getFilterMetadata();
    }

    protected String routingKey(EntryWrapper entryWrapper, CanalEntry.EventType eventType) {
        CanalToMqMetadata metadata = INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        if (StringUtils.isNotBlank(metadata.getName())) {
            return QueueNameUtils.customName(metadata.getName(), eventType);
        }
        Class<?> domainType =
                INTERFACE_BEAN_SCANNER.getDomainType(INTERFACE_BEAN_SCANNER.getInterfaceBeanClass(entryWrapper));
        CanalTableMetadata tableMetadata = INTERFACE_BEAN_SCANNER.getCanalTableMetadata(domainType);
        return QueueNameUtils.name(tableMetadata.getSchema(), tableMetadata.getName(), eventType);
    }

    protected String json(List<CanalEntry.Column> columnList) {
        return JSON.toJSONString(CommonUtils.toMap(columnList));
    }

    protected String json(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        Map<String, String> map0 = CommonUtils.toMap(beforeColumns);
        Map<String, String> map1 = CommonUtils.toMap(afterColumns);
        List<Object> list = new ArrayList<>(2);
        list.add(map0);
        list.add(map1);
        return new JSONArray(list).toJSONString();
    }

}

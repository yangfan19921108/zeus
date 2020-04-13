package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更新事件消费者
 *
 * @author fanxuankai
 */
public class UpdateConsumer extends AbstractXxlMqConsumer {

    @Override
    public MessageInfo process(EntryWrapper entryWrapper) {
        String topic = routingKey(entryWrapper, CanalEntry.EventType.UPDATE);
        List<String> messages = entryWrapper.getAllRowDataList()
                .stream()
                .map(rowData -> json(rowData.getBeforeColumnsList(), rowData.getAfterColumnsList()))
                .collect(Collectors.toList());
        return new MessageInfo(topic, messages);
    }
}

package com.fanxuankai.zeus.canal.client.rabbit.consumer;

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
public class UpdateConsumer extends AbstractRabbitMqConsumer {

    @Override
    public MessageInfo process(EntryWrapper entryWrapper) {
        List<String> messages = entryWrapper.getAllRowDataList()
                .stream()
                .map(rowData -> json(rowData.getBeforeColumnsList(), rowData.getAfterColumnsList()))
                .collect(Collectors.toList());
        String routingKey = routingKey(entryWrapper, CanalEntry.EventType.UPDATE);
        return new MessageInfo(routingKey, messages);
    }

}

package com.fanxuankai.zeus.canal.client.rabbit.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractRabbitMqConsumer {

    @Override
    public MessageInfo process(EntryWrapper entryWrapper) {
        List<String> messages = entryWrapper.getAllRowDataList()
                .stream()
                .map(rowData -> json(rowData.getBeforeColumnsList()))
                .collect(Collectors.toList());
        String routingKey = routingKey(entryWrapper, CanalEntry.EventType.DELETE);
        return new MessageInfo(routingKey, messages);
    }

}

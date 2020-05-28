package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.util.MqUtils;
import com.fanxuankai.zeus.mq.broker.core.Event;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
public interface DeleteProcessable extends Processable {

    /**
     * 删除事件处理
     *
     * @param entryWrapper 数据
     * @return 消息
     */
    @Override
    default List<Event> process(EntryWrapper entryWrapper) {
        String name = MqUtils.routingKey(entryWrapper, CanalEntry.EventType.DELETE);
        return entryWrapper.getAllRowDataList()
                .stream()
                .map(rowData -> new Event().setName(name)
                        .setKey(CommonUtils.md5(rowData.getBeforeColumnsList()))
                        .setData(CommonUtils.json(rowData.getBeforeColumnsList())))
                .collect(Collectors.toList());
    }
}

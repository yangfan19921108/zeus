package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import com.fanxuankai.zeus.canal.client.mq.core.util.MqUtils;

import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
public interface InsertProcessable extends Processable {

    /**
     * 新增事件处理
     *
     * @param entryWrapper 数据
     * @return 消息
     */
    @Override
    default MessageInfo process(EntryWrapper entryWrapper) {
        return new MessageInfo(MqUtils.routingKey(entryWrapper, CanalEntry.EventType.INSERT),
                entryWrapper.getAllRowDataList()
                        .stream()
                        .map(rowData -> {
                            MessageInfo.Message message = new MessageInfo.Message();
                            message.setMd5(MqUtils.md5(rowData.getAfterColumnsList()));
                            message.setData(MqUtils.json(rowData.getAfterColumnsList()));
                            return message;
                        })
                        .collect(Collectors.toList())
        );
    }

}

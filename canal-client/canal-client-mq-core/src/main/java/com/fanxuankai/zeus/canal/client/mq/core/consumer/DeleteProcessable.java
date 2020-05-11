package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import com.fanxuankai.zeus.canal.client.mq.core.util.MqUtils;

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
    default MessageInfo process(EntryWrapper entryWrapper) {
        return new MessageInfo(MqUtils.routingKey(entryWrapper, CanalEntry.EventType.DELETE),
                entryWrapper.getAllRowDataList()
                        .parallelStream()
                        .map(rowData -> {
                            MessageInfo.Message message = new MessageInfo.Message();
                            message.setMd5(MqUtils.md5(rowData.getBeforeColumnsList()));
                            message.setData(MqUtils.json(rowData.getBeforeColumnsList()));
                            return message;
                        })
                        .collect(Collectors.toList()));
    }
}

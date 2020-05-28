package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.mq.broker.core.Event;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface Processable extends MessageConsumer<List<Event>> {

    /**
     * 事件数据处理
     *
     * @param entryWrapper 数据
     * @return 消息
     */
    @Override
    List<Event> process(EntryWrapper entryWrapper);

}

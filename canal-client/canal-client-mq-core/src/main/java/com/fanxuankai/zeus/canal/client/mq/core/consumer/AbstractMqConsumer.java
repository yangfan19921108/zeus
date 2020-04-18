package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;

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

}

package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractMqConsumer implements DeleteProcessable {

    public DeleteConsumer(EventPublisher eventPublisher) {
        super(eventPublisher);
    }
}

package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;

/**
 * 更新事件消费者
 *
 * @author fanxuankai
 */
public class UpdateConsumer extends AbstractMqConsumer implements UpdateProcessable {

    public UpdateConsumer(EventPublisher eventPublisher) {
        super(eventPublisher);
    }
}

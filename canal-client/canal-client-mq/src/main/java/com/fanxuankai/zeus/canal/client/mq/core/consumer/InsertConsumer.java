package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;

/**
 * 新增事件消费者
 *
 * @author fanxuankai
 */
public class InsertConsumer extends AbstractMqConsumer implements InsertProcessable {

    public InsertConsumer(EventPublisher eventPublisher) {
        super(eventPublisher);
    }
}

package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.mq.core.consumer.AbstractMqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.model.MessageInfo;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;

/**
 * XXL-MQ 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractXxlMqConsumer extends AbstractMqConsumer {

    @Override
    public void consume(MessageInfo messageInfo) {
        messageInfo.getMessages().forEach(s -> XxlMqProducer.produce(new XxlMqMessage(messageInfo.getRoutingKey(), s)));
    }

}

package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.produce.AbstractMqProducer;
import com.xxl.mq.client.message.XxlMqMessage;

/**
 * @author fanxuankai
 */
public class XxlMqProducer extends AbstractMqProducer {

    @Override
    public void accept(Event event) {
        com.xxl.mq.client.producer.XxlMqProducer.produce(new XxlMqMessage(event.getName(),
                JSON.toJSONString(event)));
    }

    @Override
    public boolean isPublisherCallback() {
        return false;
    }

}

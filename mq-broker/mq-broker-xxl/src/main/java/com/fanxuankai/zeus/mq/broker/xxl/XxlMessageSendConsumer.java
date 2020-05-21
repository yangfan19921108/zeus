package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fanxuankai
 */
@Slf4j
public class XxlMessageSendConsumer implements MessageSendConsumer {

    @Override
    public void accept(MqBrokerMessage message) {
        XxlMqProducer.produce(new XxlMqMessage(message.getQueue(),
                JSON.toJSONString(new Event(message.getQueue(), message.getCode(), message.getContent()))));
    }

}

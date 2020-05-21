package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.AbstractMessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fanxuankai
 */
@Slf4j
public class XxlMessageSendConsumer extends AbstractMessageSendConsumer {

    @Override
    protected void onAccept(Event event) {
        XxlMqProducer.produce(new XxlMqMessage(event.getName(), JSON.toJSONString(event)));
    }

}

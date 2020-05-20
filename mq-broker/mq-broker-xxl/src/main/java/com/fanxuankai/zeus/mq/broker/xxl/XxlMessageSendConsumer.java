package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.producer.XxlMqProducer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fanxuankai
 */
@Slf4j
public class XxlMessageSendConsumer implements MessageSendConsumer {

    @Override
    public void accept(MessageSend messageSend) {
        XxlMqProducer.produce(new XxlMqMessage(messageSend.getQueue(),
                JSON.toJSONString(new Event(messageSend.getQueue(), messageSend.getCode(), messageSend.getContent()))));
    }

}

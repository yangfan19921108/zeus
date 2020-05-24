package com.fanxuankai.zeus.mq.broker.core.produce;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;

import javax.annotation.Resource;
import java.util.function.Consumer;

/**
 * @author fanxuankai
 */
public abstract class AbstractMqProducer implements MqProducer<MsgSend>, Consumer<Event> {

    @Resource
    private MsgSendService msgSendService;

    @Override
    public void produce(MsgSend msg) {
        accept(new Event().setName(msg.getTopic()).setKey(msg.getCode()).setData(msg.getData()));
        if (!isPublisherCallback()) {
            msgSendService.success(msg);
        }
    }

}

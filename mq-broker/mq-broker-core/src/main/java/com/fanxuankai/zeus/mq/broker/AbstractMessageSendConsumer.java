package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.service.MqBrokerMessageService;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
public abstract class AbstractMessageSendConsumer implements MessageSendConsumer {
    @Resource
    private MqBrokerMessageService mqBrokerMessageService;

    @Override
    public void accept(MqBrokerMessage message) {
        onAccept(new Event(message.getQueue(), message.getCode(), message.getContent()));
        mqBrokerMessageService.setSuccess(message);
    }

    /**
     * 生产实现(策略方法)
     *
     * @param event 事件
     */
    protected abstract void onAccept(Event event);
}

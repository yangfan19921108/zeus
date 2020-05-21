package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.EventListenerStrategy;
import com.fanxuankai.zeus.mq.broker.core.MessageReceiveConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.service.MqBrokerMessageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanxuankai
 */
public abstract class AbstractMessageReceiveConsumer implements MessageReceiveConsumer {
    @Resource
    private EventListenerFactory eventListenerFactory;
    @Resource
    private MqBrokerMessageService mqBrokerMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void accept(MqBrokerMessage message) {
        List<EventListener> eventListeners = eventListenerFactory.get(message.getQueue());
        if (CollectionUtils.isEmpty(eventListeners)) {
            return;
        }
        Event event = new Event(message.getQueue(), message.getCode(), message.getContent());
        onAccept(event, eventListeners);
        mqBrokerMessageService.setSuccess(message);
    }

    /**
     * 消费实现(策略方法)
     *
     * @param event          事件
     * @param eventListeners 事件监听器
     */
    protected abstract void onAccept(Event event, List<EventListener> eventListeners);

    /**
     * 适用的事件监听策略
     *
     * @return 事件监听策略
     */
    public abstract EventListenerStrategy getEventListenerStrategy();

}

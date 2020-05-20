package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventHandleException;
import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.EventListenerStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class DefaultMessageReceiveConsumer extends AbstractMessageReceiveConsumer {

    @Override
    protected void onAccept(Event event, List<EventListener> eventListeners) {
        for (EventListener eventListener : eventListeners) {
            try {
                eventListener.onEvent(event);
                return;
            } catch (Exception e) {
                log.error("事件处理异常", e);
            }
        }
        throw new EventHandleException("所有事件监听者均处理失败");
    }

    @Override
    public EventListenerStrategy getEventListenerStrategy() {
        return EventListenerStrategy.DEFAULT;
    }
}

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
public class AtLeastOnceMessageReceiveConsumer extends AbstractMessageReceiveConsumer {

    @Override
    protected void onAccept(Event event, List<EventListener> eventListeners) {
        boolean success = false;
        for (EventListener eventListener : eventListeners) {
            try {
                eventListener.onEvent(event);
                success = true;
            } catch (Exception e) {
                log.error("事件处理异常", e);
            }
        }
        if (success) {
            return;
        }
        throw new EventHandleException("所有事件监听者均处理失败");
    }

    @Override
    public EventListenerStrategy getEventListenerStrategy() {
        return EventListenerStrategy.AT_LEAST_ONCE;
    }
}

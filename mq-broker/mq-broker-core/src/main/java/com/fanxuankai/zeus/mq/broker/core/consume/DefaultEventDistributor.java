package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventHandleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class DefaultEventDistributor extends AbstractEventDistributor {

    @Override
    protected void onEvent(Event event, List<EventListener> eventListeners) {
        Throwable throwable = null;
        for (EventListener eventListener : eventListeners) {
            try {
                eventListener.onEvent(event);
                return;
            } catch (Exception e) {
                log.error("事件处理异常", e);
                throwable = e;
            }
        }
        throw new EventHandleException(throwable);
    }

    @Override
    public EventStrategy getEventListenerStrategy() {
        return EventStrategy.DEFAULT;
    }
}

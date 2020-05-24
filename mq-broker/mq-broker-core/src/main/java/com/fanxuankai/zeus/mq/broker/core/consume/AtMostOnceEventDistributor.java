package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.core.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class AtMostOnceEventDistributor extends AbstractEventDistributor {

    @Override
    protected void onEvent(Event event, List<EventListener> eventListeners) {
        for (EventListener eventListener : eventListeners) {
            try {
                eventListener.onEvent(event);
                break;
            } catch (Exception e) {
                log.error("事件处理异常", e);
            }
        }
    }

    @Override
    public EventStrategy getEventListenerStrategy() {
        return EventStrategy.AT_MOST_ONCE;
    }

}

package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.Event;
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
public class AtMostManyMessageReceiveConsumer extends AbstractMessageReceiveConsumer {

    @Override
    protected void onAccept(Event event, List<EventListener> eventListeners) {
        eventListeners.forEach(eventListener -> {
            try {
                eventListener.onEvent(event);
            } catch (Exception e) {
                log.error("事件处理异常", e);
            }
        });
    }

    @Override
    public EventListenerStrategy getEventListenerStrategy() {
        return EventListenerStrategy.AT_MOST_MANY;
    }
}

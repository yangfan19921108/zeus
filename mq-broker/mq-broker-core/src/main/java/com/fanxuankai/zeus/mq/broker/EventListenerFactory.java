package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.Listener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
public class EventListenerFactory implements ApplicationContextAware {

    private Map<Listener, List<EventListener>> eventListenerMap;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        eventListenerMap = applicationContext.getBeansWithAnnotation(Listener.class)
                .values()
                .stream()
                .filter(o -> o instanceof EventListener)
                .map(o -> (EventListener) o)
                .collect(Collectors.groupingBy(eventListener -> eventListener.getClass().getAnnotation(Listener.class)));
    }

    public Set<Listener> getAllListener() {
        return eventListenerMap.keySet();
    }

    public List<EventListener> get(String queue) {
        return eventListenerMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().event(), queue))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());
    }

}

package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.Listener;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class EventListenerFactory implements ApplicationContextAware {

    private static final List<Listener> LISTENERS;

    static {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages("")
                        .setScanners(new TypeAnnotationsScanner())
                );
        Stopwatch sw = Stopwatch.createStarted();
        Set<Class<?>> set = r.getTypesAnnotatedWith(Listener.class, true);
        sw.stop();
        log.info("Finished Listener scanning in {}ms, Found {}.", sw.elapsed(TimeUnit.MILLISECONDS), set.size());
        LISTENERS = new ArrayList<>(set
                .stream()
                .filter(EventListener.class::isAssignableFrom)
                .map(o -> o.getAnnotation(Listener.class))
                .collect(Collectors.toMap(Listener::event, o -> o, (o, o2) -> o))
                .values());
    }

    private Map<Listener, List<EventListener>> eventListenerMap;

    public static List<Listener> getListeners() {
        return LISTENERS;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        eventListenerMap = applicationContext.getBeansWithAnnotation(Listener.class)
                .values()
                .stream()
                .filter(o -> o instanceof EventListener)
                .map(o -> (EventListener) o)
                .collect(Collectors.groupingBy(eventListener -> eventListener.getClass().getAnnotation(Listener.class)));
    }

    public List<EventListener> get(String queue) {
        return eventListenerMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().event(), queue))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());
    }

}

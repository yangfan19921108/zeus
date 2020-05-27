package com.fanxuankai.zeus.mq.broker.core;

import com.fanxuankai.zeus.mq.broker.core.consume.EventListener;
import com.fanxuankai.zeus.mq.broker.core.consume.Listener;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 事件注册
 *
 * @author fanxuankai
 */
@Slf4j
public class EventRegistry {

    private static final Set<String> EVENTS = new HashSet<>();
    private static final Map<String, List<EventListener>> EVENT_LISTENERS = new HashMap<>();

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
        set.stream()
                .filter(com.fanxuankai.zeus.mq.broker.core.consume.EventListener.class::isAssignableFrom)
                .map(o -> o.getAnnotation(Listener.class).event())
                .forEach(EventRegistry::register);
    }

    public static Set<String> allEvent() {
        return EVENTS;
    }

    public static void register(String event) {
        EVENTS.add(event);
    }

    public static void addListener(String event, EventListener eventListener) {
        register(event);
        EVENT_LISTENERS.computeIfAbsent(event, s -> new ArrayList<>()).add(eventListener);
    }

    public static List<EventListener> getListeners(String event) {
        return EVENT_LISTENERS.get(event);
    }

}

package com.fanxuankai.zeus.mq.broker.core;

import java.util.List;

/**
 * 事件发布者
 *
 * @author fanxuankai
 */
public interface EventPublisher {

    /**
     * 单个事件
     *
     * @param event 事件
     */
    void publish(Event event);

    /**
     * 批量事件
     *
     * @param events 事件
     */
    void publish(List<Event> events);
}

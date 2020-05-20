package com.fanxuankai.zeus.mq.broker.core;

/**
 * 事件监听者
 *
 * @author fanxuankai
 */
@FunctionalInterface
public interface EventListener {
    /**
     * 监听
     *
     * @param event 事件
     */
    void onEvent(Event event);
}

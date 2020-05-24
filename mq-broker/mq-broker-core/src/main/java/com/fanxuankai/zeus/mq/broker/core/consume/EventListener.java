package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.core.Event;

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

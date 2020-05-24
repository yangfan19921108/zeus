package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.core.Event;

/**
 * 事件分发器
 *
 * @author fanxuankai
 */
public interface EventDistributor {
    /**
     * 分发
     *
     * @param event 事件
     */
    void distribute(Event event);
}

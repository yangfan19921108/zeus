package com.fanxuankai.zeus.mq.broker.core;

import java.util.function.Consumer;

/**
 * 事件消费者
 *
 * @author fanxuankai
 */
public interface EventConsumer extends Consumer<Event> {

}

package com.fanxuankai.zeus.mq.broker.core;

import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;

import java.util.function.Consumer;

/**
 * @author fanxuankai
 */
public interface MessageReceiveConsumer extends Consumer<MessageReceive> {

}

package com.fanxuankai.zeus.mq.broker.core;

import com.fanxuankai.zeus.mq.broker.domain.MessageSend;

import java.util.function.Consumer;

/**
 * @author fanxuankai
 */
public interface MessageSendConsumer extends Consumer<MessageSend> {

}
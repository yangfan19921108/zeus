package com.fanxuankai.zeus.mq.broker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public enum MessageType {
    /**
     * 发送
     */
    SEND(1),
    /**
     * 接收
     */
    RECEIVE(2),
    ;
    private final int code;
}
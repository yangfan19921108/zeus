package com.fanxuankai.zeus.canal.client.mq.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 消息实体
 *
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class MessageInfo {
    private final String routingKey;
    private final List<String> messages;
}
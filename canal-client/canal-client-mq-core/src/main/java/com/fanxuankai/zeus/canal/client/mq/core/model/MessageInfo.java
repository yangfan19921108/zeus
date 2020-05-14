package com.fanxuankai.zeus.canal.client.mq.core.model;

import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 消息实体
 *
 * @author fanxuankai
 */
@Builder
@Getter
public class MessageInfo {
    private final EntryWrapper raw;
    private final String routingKey;
    private final List<Message> messages;

    @Data
    public static class Message {
        /**
         * 消息摘要
         */
        private String md5;
        /**
         * json 字符串
         */
        private String data;
    }
}
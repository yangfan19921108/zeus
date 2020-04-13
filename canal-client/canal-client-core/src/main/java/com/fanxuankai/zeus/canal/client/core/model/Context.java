package com.fanxuankai.zeus.canal.client.core.model;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Getter;

/**
 * @author fanxuankai
 */
public class Context {
    private final CanalConnector canalConnector;
    @Getter
    private final Message message;

    public Context(CanalConnector canalConnector, Message message) {
        this.canalConnector = canalConnector;
        this.message = message;
    }

    public void ack() {
        canalConnector.ack(message.getId());
    }

    public void rollback() {
        canalConnector.rollback(message.getId());
    }
}

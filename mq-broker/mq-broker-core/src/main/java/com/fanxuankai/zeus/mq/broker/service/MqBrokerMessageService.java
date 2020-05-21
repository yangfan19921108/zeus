package com.fanxuankai.zeus.mq.broker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.enums.MessageType;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface MqBrokerMessageService extends IService<MqBrokerMessage> {

    /**
     * 获取消息且锁定, 将状态更改为 SendStatus.SENDING
     *
     * @param queue       队列名
     * @param count       锁定数量
     * @param maxRetry    最大重试次数
     * @param messageType 消息类型
     * @return 锁定的消息
     */
    List<MqBrokerMessage> getAndLock(String queue, long count, int maxRetry, MessageType messageType);

    /**
     * 发送成功
     *
     * @param message 消息
     */
    void setSuccess(MqBrokerMessage message);

    /**
     * 发送失败
     *
     * @param message 消息
     */
    void setFailure(MqBrokerMessage message);
}

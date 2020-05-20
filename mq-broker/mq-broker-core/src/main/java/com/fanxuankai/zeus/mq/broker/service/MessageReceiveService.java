package com.fanxuankai.zeus.mq.broker.service;

import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface MessageReceiveService {
    /**
     * 获取消息且锁定, 将状态更改为 ReceiveStatus.CONSUMING
     *
     * @param queue    队列名
     * @param count    锁定数量
     * @param maxRetry 最大重试次数
     * @return 锁定的消息
     */
    List<MessageReceive> getAndLock(String queue, long count, int maxRetry);

    /**
     * 发送成功
     *
     * @param messageReceive 消息
     */
    void setSuccess(MessageReceive messageReceive);

    /**
     * 发送失败
     *
     * @param messageReceive 消息
     */
    void setFailure(MessageReceive messageReceive);
}

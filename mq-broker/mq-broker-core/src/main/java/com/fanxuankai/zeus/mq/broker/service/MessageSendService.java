package com.fanxuankai.zeus.mq.broker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface MessageSendService extends IService<MessageSend> {
    /**
     * 获取消息且锁定, 将状态更改为 SendStatus.SENDING
     *
     * @param queue    队列名
     * @param count    锁定数量
     * @param maxRetry 最大重试次数
     * @return 锁定的消息
     */
    List<MessageSend> getAndLock(String queue, long count, int maxRetry);

    /**
     * 发送成功
     *
     * @param messageSend 消息
     */
    void setSuccess(MessageSend messageSend);

    /**
     * 发送失败
     *
     * @param messageSend 消息
     */
    void setFailure(MessageSend messageSend);
}

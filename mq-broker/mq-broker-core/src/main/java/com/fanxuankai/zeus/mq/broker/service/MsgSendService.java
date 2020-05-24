package com.fanxuankai.zeus.mq.broker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface MsgSendService extends IService<MsgSend> {

    /**
     * 获取消息
     *
     * @return list
     */
    List<MsgSend> pullData();

    /**
     * 回调超时
     */
    void publisherCallbackTimeout();

    /**
     * success
     *
     * @param msg 消息
     */
    void success(MsgSend msg);

    /**
     * success
     *
     * @param topic 主题
     * @param code  code
     */
    void success(String topic, String code);

    /**
     * failure
     *
     * @param topic 主题
     * @param code  code
     * @param cause 原因
     */
    void failure(String topic, String code, String cause);

    /**
     * failure
     *
     * @param msg   消息
     * @param cause 原因
     */
    void failure(MsgSend msg, String cause);

}

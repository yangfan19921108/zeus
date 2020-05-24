package com.fanxuankai.zeus.mq.broker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;

import java.util.List;

/**
 * @author fanxuankai
 */
public interface MsgReceiveService extends IService<MsgReceive> {

    /**
     * 获取消息
     *
     * @return list
     */
    List<MsgReceive> pullData();

    /**
     * 成功
     *
     * @param msg 消息
     */
    void consumed(MsgReceive msg);

    /**
     * nack
     *
     * @param msg   消息
     * @param cause 原因
     */
    void unconsumed(MsgReceive msg, String cause);

}

package com.fanxuankai.zeus.mq.broker.constants;

import com.fanxuankai.zeus.mq.broker.task.MsgReceiveTask;
import com.fanxuankai.zeus.mq.broker.task.MsgSendTask;

/**
 * @author fanxuankai
 */
public class LockResourceConstants {
    public static final String MSG_SEND_TASK = MsgSendTask.class.getName();
    public static final String MSG_RECEIVE_TASK = MsgReceiveTask.class.getName();
    public static final String PUBLISHER_CALLBACK_TIMEOUT = "PublisherCallbackTimeout";
}

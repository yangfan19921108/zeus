package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.UpdateProcessable;

/**
 * 更新事件消费者
 *
 * @author fanxuankai
 */
public class UpdateConsumer extends AbstractXxlMqConsumer implements UpdateProcessable {
    public UpdateConsumer(ApplicationInfo applicationInfo) {
        super(applicationInfo);
    }
}

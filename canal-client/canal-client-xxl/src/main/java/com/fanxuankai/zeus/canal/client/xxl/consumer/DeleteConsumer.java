package com.fanxuankai.zeus.canal.client.xxl.consumer;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.DeleteProcessable;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractXxlMqConsumer implements DeleteProcessable {

    public DeleteConsumer(ApplicationInfo applicationInfo) {
        super(applicationInfo);
    }
}

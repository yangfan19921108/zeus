package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

/**
 * Canal 事务确认订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class ConfirmSubscriber implements Flow.Subscriber<ContextWrapper> {
    private final Otter otter;
    private final ApplicationInfo applicationInfo;
    private Flow.Subscription subscription;

    public ConfirmSubscriber(Otter otter, ApplicationInfo applicationInfo) {
        this.otter = otter;
        this.applicationInfo = applicationInfo;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(ContextWrapper item) {
        long l = System.currentTimeMillis();
        item.confirm();
        long l1 = System.currentTimeMillis();
        if (!item.getMessageWrapper().getEntryWrapperList().isEmpty()) {
            log.info("{} Confirm batchId: {} time: {}ms", applicationInfo.uniqueString(),
                    item.getMessageWrapper().getBatchId(), l1 - l);
        }
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", applicationInfo.uniqueString(), throwable.getLocalizedMessage()), throwable);
        this.otter.stop();
    }

    @Override
    public void onComplete() {
        log.info("{} Done", applicationInfo.uniqueString());
    }
}
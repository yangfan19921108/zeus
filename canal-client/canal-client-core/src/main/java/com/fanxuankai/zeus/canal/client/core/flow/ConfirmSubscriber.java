package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import com.fanxuankai.zeus.util.concurrent.Flow;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Canal 事务确认订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class ConfirmSubscriber implements Flow.Subscriber<ContextWrapper> {

    private final Otter otter;
    private final Config config;
    private final CanalProperties canalProperties;
    private Flow.Subscription subscription;

    public ConfirmSubscriber(Otter otter, Config config) {
        this.otter = otter;
        this.config = config;
        this.canalProperties = ApplicationContexts.getBean(CanalProperties.class);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(ContextWrapper item) {
        Stopwatch sw = Stopwatch.createStarted();
        item.confirm();
        sw.stop();
        if (canalProperties.isShowEventLog() && !item.getMessageWrapper().getEntryWrapperList().isEmpty()) {
            log.info("{} Confirm batchId: {} time: {}ms", config.getApplicationInfo().uniqueString(),
                    item.getMessageWrapper().getBatchId(), sw.elapsed(TimeUnit.MILLISECONDS));
        }
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", config.getApplicationInfo().uniqueString(), throwable.getLocalizedMessage()),
                throwable);
        this.subscription.cancel();
        this.otter.stop();
    }

    @Override
    public void onComplete() {
        log.info("{} Done", config.getApplicationInfo().uniqueString());
    }
}

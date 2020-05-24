package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import com.fanxuankai.zeus.util.concurrent.Flow;
import com.fanxuankai.zeus.util.concurrent.SubmissionPublisher;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 消息转换订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class ConvertProcessor extends SubmissionPublisher<ContextWrapper>
        implements Flow.Processor<Context, ContextWrapper> {

    private final Otter otter;
    private final Config config;
    private final CanalProperties canalProperties;
    private Flow.Subscription subscription;

    public ConvertProcessor(Otter otter, Config config) {
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
    public void onNext(Context item) {
        Stopwatch sw = Stopwatch.createStarted();
        ContextWrapper wrapper = new ContextWrapper(item);
        sw.stop();
        if (canalProperties.isShowEventLog() && !item.getMessage().getEntries().isEmpty()) {
            log.info("{} Convert batchId: {} time: {}ms", config.getApplicationInfo().uniqueString(),
                    item.getMessage().getId(), sw.elapsed(TimeUnit.MILLISECONDS));
        }
        submit(wrapper);
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

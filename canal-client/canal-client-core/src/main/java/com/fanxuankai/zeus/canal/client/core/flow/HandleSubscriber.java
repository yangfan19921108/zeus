package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.util.concurrent.Flow;
import com.fanxuankai.zeus.util.concurrent.SubmissionPublisher;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 处理订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class HandleSubscriber extends SubmissionPublisher<ContextWrapper> implements Flow.Subscriber<ContextWrapper> {

    private final Otter otter;
    private final Config config;

    public HandleSubscriber(Otter otter, Config config) {
        this.otter = otter;
        this.config = config;
    }

    @Override
    public void onNext(ContextWrapper item) {
        if (!config.isSkip()) {
            Stopwatch sw = Stopwatch.createStarted();
            config.getHandler().handle(item.getMessageWrapper());
            sw.stop();
            if (Objects.equals(config.getCanalConfig().getShowEventLog(), Boolean.TRUE)
                    && !item.getMessageWrapper().getEntryWrapperList().isEmpty()) {
                log.info("{} Handle batchId: {} time: {}ms", config.getApplicationInfo().uniqueString(),
                        item.getMessageWrapper().getBatchId(), sw.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        submit(item);
    }

    @Override
    public void onComplete() {
        stop();
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", config.getApplicationInfo().uniqueString(), throwable.getLocalizedMessage()),
                throwable);
        onComplete();
        this.otter.stop();
    }
}

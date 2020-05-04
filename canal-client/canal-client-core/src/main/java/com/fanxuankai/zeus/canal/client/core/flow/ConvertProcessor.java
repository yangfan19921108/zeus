package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

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
    private Flow.Subscription subscription;

    public ConvertProcessor(Otter otter, Config config) {
        this.otter = otter;
        this.config = config;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Context item) {
        long l = System.currentTimeMillis();
        ContextWrapper wrapper = new ContextWrapper(item);
        long l1 = System.currentTimeMillis();
        if (Objects.equals(config.getCanalConfig().getShowEventLog(), Boolean.TRUE)
                && !item.getMessage().getEntries().isEmpty()) {
            log.info("{} Convert batchId: {} time: {}ms", config.getApplicationInfo().uniqueString(),
                    item.getMessage().getId(),
                    l1 - l);
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

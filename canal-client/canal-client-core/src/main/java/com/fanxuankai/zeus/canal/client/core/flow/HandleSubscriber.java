package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.execption.HandleException;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.Handler;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 处理订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class HandleSubscriber extends SubmissionPublisher<ContextWrapper> implements Flow.Processor<ContextWrapper,
        ContextWrapper> {
    private final Otter otter;
    private final Config config;
    private Flow.Subscription subscription;

    public HandleSubscriber(Otter otter, Config config) {
        this.otter = otter;
        this.config = config;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(ContextWrapper item) {
        if (!config.skip) {
            try {
                long l = System.currentTimeMillis();
                config.handler.handle(item.getMessageWrapper());
                long l1 = System.currentTimeMillis();
                if (!item.getMessageWrapper().getEntryWrapperList().isEmpty()) {
                    log.info("{} Handle batchId: {} time: {}ms", config.applicationInfo.uniqueString(),
                            item.getMessageWrapper().getBatchId(), l1 - l);
                }
            } catch (HandleException e) {
                log.error(e.getLocalizedMessage(), e);
                item.setHandleError(true);
            } finally {
                submit(item);
            }
        }
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", config.applicationInfo.uniqueString(), throwable.getLocalizedMessage()),
                throwable);
        this.otter.stop();
    }

    @Override
    public void onComplete() {
        log.info("{} Done", config.applicationInfo.uniqueString());
    }

    @AllArgsConstructor
    @Getter
    public static class Config {
        // 消息处理器
        private final Handler<MessageWrapper> handler;
        private final ApplicationInfo applicationInfo;
        // 是否跳过而不处理
        private final boolean skip;
    }
}

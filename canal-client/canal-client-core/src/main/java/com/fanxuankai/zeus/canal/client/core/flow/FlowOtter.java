package com.fanxuankai.zeus.canal.client.core.flow;

import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.protocol.AbstractOtter;
import com.fanxuankai.zeus.util.concurrent.SubmissionPublisher;
import lombok.extern.slf4j.Slf4j;

/**
 * Otter 并行流客户端
 *
 * @author fanxuankai
 */
@Slf4j
public class FlowOtter extends AbstractOtter {

    private final SubmissionPublisher<Context> publisher;
    private final Config config;

    public FlowOtter(Config config) {
        super(config.getConnectConfig());
        this.config = config;
        publisher = new SubmissionPublisher<>();
        ConvertProcessor converter = new ConvertProcessor(this, config);
        FilterSubscriber filter = new FilterSubscriber(this, config);
        HandleSubscriber handler = new HandleSubscriber(this, config);
        ConfirmSubscriber confirm = new ConfirmSubscriber(this, config);
        publisher.subscribe(converter);
        converter.subscribe(filter);
        filter.subscribe(handler);
        handler.subscribe(confirm);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void process(Context context) {
        if (config.isSkip()) {
            try {
                context.ack();
            } catch (CanalClientException e) {
                context.rollback();
                log.error("Canal ack failure", e);
            }
        } else {
            publisher.submit(context);
        }
    }

    @Override
    public void stop() {
        super.stop();
        publisher.close();
    }

}

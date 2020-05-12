package com.fanxuankai.zeus.canal.client.core.flow;

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
        super(config.getConnectConfig(), config.getCanalConfig());
        this.config = config;
        publisher = new SubmissionPublisher<>();
    }

    @Override
    public void start() {
        ConvertProcessor converter = new ConvertProcessor(this, config);
        FilterSubscriber filter = new FilterSubscriber(this, config);
        HandleSubscriber handler = new HandleSubscriber(this, config);
        ConfirmSubscriber confirm = new ConfirmSubscriber(this, config);

        publisher.subscribe(converter);
        converter.subscribe(filter);
        filter.subscribe(handler);
        handler.subscribe(confirm);

        super.start();
    }

    @Override
    protected void process(Context context) {
        publisher.submit(context);
    }

    @Override
    public void stop() {
        super.stop();
        publisher.close();
    }

}

package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.protocol.AbstractOtter;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.SubmissionPublisher;

/**
 * Otter 并行流客户端
 *
 * @author fanxuankai
 */
public class FlowOtter extends AbstractOtter {

    private final SubmissionPublisher<Context> publisher = new SubmissionPublisher<>();

    public FlowOtter(Config config) {
        super(config.getConnectConfig());

        // 构建 Flow

        // 流转换订阅者
        ConvertProcessor convertProcessor = new ConvertProcessor(new ConvertProcessor.Config(this,
                config.getCanalConfig(), config.hsConfig.getApplicationInfo()));
        publisher.subscribe(convertProcessor);

        // 过滤器订阅者
        FilterSubscriber filterSubscriber = new FilterSubscriber(this, config.consumerInfo);
        convertProcessor.subscribe(filterSubscriber);

        // 流处理订阅者
        HandleSubscriber handleSubscriber = new HandleSubscriber(this, config.hsConfig);
        filterSubscriber.subscribe(handleSubscriber);

        // 流确认订阅者
        ConfirmSubscriber confirmSubscriber = new ConfirmSubscriber(this, config.hsConfig.getApplicationInfo());
        handleSubscriber.subscribe(confirmSubscriber);
    }

    @Override
    public void process(Context context) {
        publisher.submit(context);
    }

    @Override
    public void stop() {
        super.stop();
        publisher.close();
    }

    @Builder
    @Getter
    public static class Config {
        private final ConnectConfig connectConfig;
        private final HandleSubscriber.Config hsConfig;
        private final ConsumerInfo consumerInfo;
        private final CanalConfig canalConfig;
    }
}

package com.fanxuankai.zeus.canal.client.xxl.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.config.CanalRunner;
import com.fanxuankai.zeus.canal.client.core.flow.FlowOtter;
import com.fanxuankai.zeus.canal.client.core.flow.HandleSubscriber;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageHandler;
import com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner;
import com.fanxuankai.zeus.canal.client.xxl.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.UpdateConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@Configuration
@Import({CanalAutoConfigurationImportRegistrar.class,
        InsertConsumer.class, UpdateConsumer.class, DeleteConsumer.class})
@EnableConfigurationProperties(CanalXxlConfig.class)
@ConditionalOnProperty(value = CanalXxlConfig.ENABLE, havingValue = "true", matchIfMissing = true)
public class CanalAutoConfiguration {

    public static final String BEHAVIOR = "XxlMQ";

    @Resource
    private CanalConfig canalConfig;

    @Bean
    @SuppressWarnings("rawtypes")
    public XxlMessageHandler xxlMessageHandler(InsertConsumer insertConsumer, UpdateConsumer updateConsumer,
                                               DeleteConsumer deleteConsumer) {
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, updateConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        MessageHandler.Config config = new MessageHandler.Config(consumerMap,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new XxlMessageHandler(config);
    }

    @Bean
    public XxlFlowOtter xxlFlowOtter(CanalXxlConfig canalXxlConfig, XxlMessageHandler messageHandler) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR);
        HandleSubscriber.Config config = new HandleSubscriber.Config(messageHandler,
                applicationInfo, canalXxlConfig.getSkip());
        ConnectConfig connectConfig = new ConnectConfig(canalXxlConfig.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        return new XxlFlowOtter(connectConfig, config);
    }

    @Bean
    public CanalRunner canalRunner(XxlFlowOtter xxlFlowOtter) {
        CanalRunner.Config config = new CanalRunner.Config(xxlFlowOtter,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new CanalRunner(config);
    }

    public static class XxlFlowOtter extends FlowOtter {
        public XxlFlowOtter(ConnectConfig connectConfig, HandleSubscriber.Config handleSubscriberConfig) {
            super(connectConfig, handleSubscriberConfig);
        }
    }

    public static class XxlMessageHandler extends MessageHandler {
        public XxlMessageHandler(Config config) {
            super(config);
        }
    }


}

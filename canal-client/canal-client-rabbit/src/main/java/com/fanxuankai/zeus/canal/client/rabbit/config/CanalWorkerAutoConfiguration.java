package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.config.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.FlowOtter;
import com.fanxuankai.zeus.canal.client.core.flow.HandleSubscriber;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageHandler;
import com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.UpdateConsumer;
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
@Import({InsertConsumer.class, UpdateConsumer.class, DeleteConsumer.class})
@EnableConfigurationProperties(CanalRabbitProperties.class)
@ConditionalOnProperty(value = CanalRabbitProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    private static final String BEHAVIOR = "RabbitMQ";
    private static final String CONSUMER_INFO_NAME = "rabbitConsumerInfo";
    private static final String MESSAGE_HANDLER_NAME = "rabbitMessageHandler";
    private static final String FLOW_OTTER_NAME = "rabbitFlowOtter";

    @Resource
    private CanalConfig canalConfig;
    @Resource(name = CONSUMER_INFO_NAME)
    private ConsumerInfo consumerInfo;
    @Resource(name = MESSAGE_HANDLER_NAME)
    private MessageHandler messageHandler;
    @Resource(name = FLOW_OTTER_NAME)
    private FlowOtter flowOtter;

    @Bean(CONSUMER_INFO_NAME)
    public ConsumerInfo consumerInfo(InsertConsumer insertConsumer,
                                     UpdateConsumer updateConsumer,
                                     DeleteConsumer deleteConsumer) {
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, updateConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        return new ConsumerInfo(consumerMap, new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
    }

    @Bean(MESSAGE_HANDLER_NAME)
    public MessageHandler messageHandler() {
        return new MessageHandler(consumerInfo);
    }

    @Bean(FLOW_OTTER_NAME)
    public FlowOtter flowOtter(CanalRabbitProperties canalRabbitConfig) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR);
        HandleSubscriber.Config config = new HandleSubscriber.Config(messageHandler,
                applicationInfo, canalRabbitConfig.getSkip());
        ConnectConfig connectConfig = new ConnectConfig(canalRabbitConfig.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        return new FlowOtter(FlowOtter.Config.builder()
                .canalConfig(canalConfig)
                .connectConfig(connectConfig)
                .consumerInfo(consumerInfo)
                .hsConfig(config)
                .build());
    }

    @Bean("rabbitCanalWorker")
    public CanalWorker canalWorker() {
        CanalWorker.Config config = new CanalWorker.Config(flowOtter,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new CanalWorker(config);
    }

}

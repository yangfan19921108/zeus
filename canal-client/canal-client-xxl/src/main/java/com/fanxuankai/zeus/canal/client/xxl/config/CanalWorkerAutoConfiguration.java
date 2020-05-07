package com.fanxuankai.zeus.canal.client.xxl.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.config.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.flow.FlowOtter;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageHandler;
import com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner;
import com.fanxuankai.zeus.canal.client.xxl.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.UpdateConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@Import({InsertConsumer.class, UpdateConsumer.class, DeleteConsumer.class})
@EnableConfigurationProperties(CanalXxlProperties.class)
@ConditionalOnProperty(value = CanalXxlProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    private static final String BEHAVIOR = "XxlMQ";
    private static final String APPLICATION_INFO_NAME = "xxlApplicationInfo";
    private static final String CONSUMER_INFO_NAME = "xxlConsumerInfo";
    private static final String MESSAGE_HANDLER_NAME = "xxlMessageHandler";
    private static final String FLOW_OTTER_NAME = "xxlFlowOtter";

    private final CanalConfig canalConfig;

    public CanalWorkerAutoConfiguration(CanalConfig canalConfig) {
        this.canalConfig = canalConfig;
    }

    @Bean(APPLICATION_INFO_NAME)
    public ApplicationInfo applicationInfo() {
        return new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR);
    }

    @Bean
    public InsertConsumer insertConsumer(@Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        return new InsertConsumer(applicationInfo);
    }

    @Bean
    public UpdateConsumer updateConsumer(@Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        return new UpdateConsumer(applicationInfo);
    }

    @Bean
    public DeleteConsumer deleteConsumer(@Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        return new DeleteConsumer(applicationInfo);
    }

    @Bean(CONSUMER_INFO_NAME)
    public ConsumerInfo consumerInfo(InsertConsumer insertConsumer, UpdateConsumer updateConsumer,
                                     DeleteConsumer deleteConsumer,
                                     @Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, updateConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        return new ConsumerInfo(consumerMap, applicationInfo);
    }

    @Bean(MESSAGE_HANDLER_NAME)
    public MessageHandler messageHandler(@Autowired @Qualifier(CONSUMER_INFO_NAME) ConsumerInfo consumerInfo) {
        return new MessageHandler(consumerInfo);
    }

    @Bean(FLOW_OTTER_NAME)
    public FlowOtter flowOtter(CanalXxlProperties canalXxlConfig,
                               @Autowired @Qualifier(CONSUMER_INFO_NAME) ConsumerInfo consumerInfo,
                               @Autowired @Qualifier(MESSAGE_HANDLER_NAME) MessageHandler messageHandler,
                               @Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        ConnectConfig connectConfig = new ConnectConfig(canalXxlConfig.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        return new FlowOtter(Config.builder()
                .applicationInfo(applicationInfo)
                .canalConfig(canalConfig)
                .connectConfig(connectConfig)
                .consumerInfo(consumerInfo)
                .handler(messageHandler)
                .skip(canalXxlConfig.getSkip())
                .build());
    }

    @Bean("xxlCanalWorker")
    public CanalWorker canalWorker(@Autowired @Qualifier(FLOW_OTTER_NAME) FlowOtter flowOtter,
                                   @Autowired @Qualifier(APPLICATION_INFO_NAME) ApplicationInfo applicationInfo) {
        CanalWorker.Config config = new CanalWorker.Config(flowOtter, applicationInfo);
        return new CanalWorker(config);
    }

}

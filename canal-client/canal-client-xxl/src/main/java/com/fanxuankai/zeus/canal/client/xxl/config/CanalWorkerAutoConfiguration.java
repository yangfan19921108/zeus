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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalXxlProperties.class)
@ConditionalOnProperty(value = CanalXxlProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("xxlMQCanalWorker")
    public CanalWorker canalWorker(CanalConfig canalConfig, CanalXxlProperties canalXxlProperties) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), "XxlMQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(applicationInfo));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(applicationInfo));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(applicationInfo));
        ConsumerInfo consumerInfo = new ConsumerInfo(consumerMap, applicationInfo);
        MessageHandler messageHandler = new MessageHandler(consumerInfo);
        ConnectConfig connectConfig = new ConnectConfig(canalXxlProperties.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        FlowOtter otter = new FlowOtter(Config.builder()
                .applicationInfo(applicationInfo)
                .connectConfig(connectConfig)
                .consumerInfo(consumerInfo)
                .handler(messageHandler)
                .skip(canalXxlProperties.getSkip())
                .build());
        return new CanalWorker(new CanalWorker.Config(otter, applicationInfo));
    }

}

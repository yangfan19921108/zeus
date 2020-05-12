package com.fanxuankai.zeus.canal.client.rabbit.config;

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
import com.fanxuankai.zeus.canal.client.rabbit.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.UpdateConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalRabbitProperties.class)
@ConditionalOnProperty(value = CanalRabbitProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("RabbitMQCanalWorker")
    public CanalWorker canalWorker(CanalConfig canalConfig, CanalRabbitProperties canalRabbitProperties) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), "RabbitMQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(applicationInfo));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(applicationInfo));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(applicationInfo));
        ConsumerInfo consumerInfo = new ConsumerInfo(consumerMap, applicationInfo);
        MessageHandler messageHandler = new MessageHandler(consumerInfo);
        ConnectConfig connectConfig = new ConnectConfig(canalRabbitProperties.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        FlowOtter otter = new FlowOtter(Config.builder()
                .applicationInfo(applicationInfo)
                .canalConfig(canalConfig)
                .connectConfig(connectConfig)
                .consumerInfo(consumerInfo)
                .handler(messageHandler)
                .skip(canalRabbitProperties.getSkip())
                .build());
        return new CanalWorker(new CanalWorker.Config(otter, applicationInfo));
    }

}

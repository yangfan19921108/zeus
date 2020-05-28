package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.UpdateConsumer;
import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalMqProperties.class)
@ConditionalOnProperty(value = CanalMqProperties.ENABLED, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("rabbitMQCanalWorker")
    public CanalWorker canalWorker(CanalProperties canalProperties,
                                   CanalMqProperties canalMqProperties,
                                   EventPublisher eventPublisher) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalProperties.getApplicationName(), "MQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(eventPublisher));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(eventPublisher));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(eventPublisher));
        ConnectConfig connectConfig = new ConnectConfig(canalMqProperties.getInstance(),
                CanalToMqScanner.CONSUME_CONFIGURATION.getFilter(), applicationInfo);
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .connectConfig(connectConfig)
                .consumerMap(consumerMap)
                .skip(canalMqProperties.getSkip())
                .build();
        return new CanalWorker(config);
    }

}

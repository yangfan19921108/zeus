package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.UpdateConsumer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalRabbitProperties.class)
@ConditionalOnProperty(value = CanalRabbitProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("rabbitMQCanalWorker")
    public CanalWorker canalWorker(CanalConfig canalConfig,
                                   CanalRabbitProperties canalRabbitProperties,
                                   RedisTemplate<Object, Object> redisTemplate,
                                   RabbitTemplate rabbitTemplate) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), "RabbitMQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(applicationInfo, redisTemplate,
                rabbitTemplate));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(applicationInfo, redisTemplate,
                rabbitTemplate));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(applicationInfo, redisTemplate,
                rabbitTemplate));
        ConnectConfig connectConfig = new ConnectConfig(canalRabbitProperties.getInstance(),
                MqConsumerScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .canalConfig(canalConfig)
                .connectConfig(connectConfig)
                .consumerMap(consumerMap)
                .redisTemplate(redisTemplate)
                .skip(canalRabbitProperties.getSkip())
                .build();
        return new CanalWorker(config);
    }

}

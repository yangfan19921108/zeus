package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.rabbit.consumer.UpdateConsumer;
import org.springframework.amqp.core.AmqpTemplate;
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
    public CanalWorker canalWorker(CanalProperties canalProperties,
                                   CanalRabbitProperties canalRabbitProperties,
                                   RedisTemplate<Object, Object> redisTemplate,
                                   AmqpTemplate amqpTemplate) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalProperties.getApplicationName(), "RabbitMQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(applicationInfo, redisTemplate,
                amqpTemplate));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(applicationInfo, redisTemplate,
                amqpTemplate));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(applicationInfo, redisTemplate,
                amqpTemplate));
        ConnectConfig connectConfig = new ConnectConfig(canalRabbitProperties.getInstance(),
                CanalToMqScanner.CONSUME_CONFIGURATION.getFilter(), applicationInfo);
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .canalProperties(canalProperties)
                .connectConfig(connectConfig)
                .consumerMap(consumerMap)
                .redisTemplate(redisTemplate)
                .skip(canalRabbitProperties.getSkip())
                .build();
        return new CanalWorker(config);
    }

}

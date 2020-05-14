package com.fanxuankai.zeus.canal.client.xxl.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.xxl.consumer.UpdateConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner.CONSUME_CONFIGURATION;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalXxlProperties.class)
@ConditionalOnProperty(value = CanalXxlProperties.ENABLE, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("xxlMQCanalWorker")
    public CanalWorker canalWorker(CanalProperties canalProperties,
                                   CanalXxlProperties canalXxlProperties,
                                   RedisTemplate<Object, Object> redisTemplate) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalProperties.getApplicationName(), "XxlMQ");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(3);
        consumerMap.put(CanalEntry.EventType.INSERT, new InsertConsumer(applicationInfo, redisTemplate));
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(applicationInfo, redisTemplate));
        consumerMap.put(CanalEntry.EventType.DELETE, new DeleteConsumer(applicationInfo, redisTemplate));
        ConnectConfig connectConfig = new ConnectConfig(canalXxlProperties.getInstance(),
                CONSUME_CONFIGURATION.getFilter(), applicationInfo);
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .canalProperties(canalProperties)
                .connectConfig(connectConfig)
                .consumerMap(consumerMap)
                .redisTemplate(redisTemplate)
                .skip(canalXxlProperties.getSkip())
                .build();
        return new CanalWorker(config);
    }

}

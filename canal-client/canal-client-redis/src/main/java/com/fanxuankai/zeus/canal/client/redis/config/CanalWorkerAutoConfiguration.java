package com.fanxuankai.zeus.canal.client.redis.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.EraseConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.UpdateConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalRedisProperties.class)
@ConditionalOnProperty(value = CanalRedisProperties.ENABLED, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("redisCanalWorker")
    public CanalWorker canalWorker(CanalProperties canalProperties,
                                   CanalRedisProperties canalRedisProperties,
                                   RedisTemplate<Object, Object> redisTemplate) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalProperties.getApplicationName(), "Redis");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(4);
        InsertConsumer insertConsumer = new InsertConsumer(redisTemplate);
        DeleteConsumer deleteConsumer = new DeleteConsumer(redisTemplate);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, new UpdateConsumer(redisTemplate, insertConsumer, deleteConsumer));
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        consumerMap.put(CanalEntry.EventType.ERASE, new EraseConsumer(redisTemplate));
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .canalProperties(canalProperties)
                .connectConfig(new ConnectConfig(canalRedisProperties.getInstance(),
                        CanalToRedisScanner.CONSUME_CONFIGURATION.getFilter(), applicationInfo))
                .consumerMap(consumerMap)
                .redisTemplate(redisTemplate)
                .skip(Boolean.FALSE)
                .build();
        return new CanalWorker(config);
    }
}

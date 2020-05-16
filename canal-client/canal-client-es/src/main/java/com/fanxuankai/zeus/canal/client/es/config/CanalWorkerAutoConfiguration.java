package com.fanxuankai.zeus.canal.client.es.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.flow.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.Config;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.es.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.es.consumer.EraseConsumer;
import com.fanxuankai.zeus.canal.client.es.consumer.InsertConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.fanxuankai.zeus.canal.client.es.config.CanalToEsScanner.CONSUME_CONFIGURATION;

/**
 * @author fanxuankai
 */
@EnableConfigurationProperties(CanalEsProperties.class)
@ConditionalOnProperty(value = CanalEsProperties.ENABLED, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    @Bean("esCanalWorker")
    public CanalWorker canalWorker(CanalProperties canalProperties,
                                   CanalEsProperties canalEsProperties,
                                   RedisTemplate<Object, Object> redisTemplate,
                                   ElasticsearchTemplate elasticsearchTemplate) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalProperties.getApplicationName(), "Es");
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(4);
        InsertConsumer insertConsumer = new InsertConsumer(elasticsearchTemplate);
        DeleteConsumer deleteConsumer = new DeleteConsumer(elasticsearchTemplate);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, insertConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        consumerMap.put(CanalEntry.EventType.ERASE, new EraseConsumer(elasticsearchTemplate));
        Config config = Config.builder()
                .applicationInfo(applicationInfo)
                .canalProperties(canalProperties)
                .connectConfig(new ConnectConfig(canalEsProperties.getInstance(),
                        CONSUME_CONFIGURATION.getFilter(), applicationInfo))
                .consumerMap(consumerMap)
                .redisTemplate(redisTemplate)
                .skip(Boolean.FALSE)
                .build();
        CanalWorker canalWorker = new CanalWorker(config);
        canalWorker.setOnStart(() ->
                CONSUME_CONFIGURATION.getDomainClasses()
                        .stream()
                        .filter(domainClass -> !elasticsearchTemplate.indexExists(domainClass))
                        .forEach(elasticsearchTemplate::createIndex));
        return canalWorker;
    }
}

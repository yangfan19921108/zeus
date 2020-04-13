package com.fanxuankai.zeus.canal.client.redis.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.config.CanalRunner;
import com.fanxuankai.zeus.canal.client.core.flow.FlowOtter;
import com.fanxuankai.zeus.canal.client.core.flow.HandleSubscriber;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageHandler;
import com.fanxuankai.zeus.canal.client.redis.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.EraseConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.InsertOrUpdateConsumer;
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
@Import({CanalAutoConfigurationImportRegistrar.class,
        InsertOrUpdateConsumer.class, DeleteConsumer.class, EraseConsumer.class})
@EnableConfigurationProperties(CanalRedisConfig.class)
@ConditionalOnProperty(value = CanalRedisConfig.ENABLED, havingValue = "true", matchIfMissing = true)
public class CanalAutoConfiguration {

    public static final String BEHAVIOR = "Redis";

    @Resource
    private CanalConfig canalConfig;

    @Bean
    @SuppressWarnings("rawtypes")
    public RedisMessageHandler redisMessageHandler(InsertOrUpdateConsumer insertOrUpdateConsumer,
                                                   DeleteConsumer deleteConsumer, EraseConsumer eraseConsumer) {
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(4);
        consumerMap.put(CanalEntry.EventType.INSERT, insertOrUpdateConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, insertOrUpdateConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        consumerMap.put(CanalEntry.EventType.ERASE, eraseConsumer);
        MessageHandler.Config config = new MessageHandler.Config(consumerMap,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new RedisMessageHandler(config);
    }

    @Bean
    public RedisFlowOtter redisFlowOtter(CanalRedisConfig canalRedisConfig,
                                         RedisMessageHandler redisMessageHandler) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR);
        HandleSubscriber.Config config = new HandleSubscriber.Config(redisMessageHandler,
                applicationInfo, false);
        ConnectConfig connectConfig = new ConnectConfig(canalRedisConfig.getInstance(),
                RedisRepositoryScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        return new RedisFlowOtter(connectConfig, config);
    }

    @Bean
    public CanalRunner redisCanalRunner(RedisFlowOtter redisFlowOtter) {
        CanalRunner.Config config = new CanalRunner.Config(redisFlowOtter,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new CanalRunner(config);
    }

    public static class RedisFlowOtter extends FlowOtter {
        public RedisFlowOtter(ConnectConfig connectConfig, HandleSubscriber.Config handleSubscriberConfig) {
            super(connectConfig, handleSubscriberConfig);
        }
    }

    public static class RedisMessageHandler extends MessageHandler {
        public RedisMessageHandler(Config config) {
            super(config);
        }
    }
}

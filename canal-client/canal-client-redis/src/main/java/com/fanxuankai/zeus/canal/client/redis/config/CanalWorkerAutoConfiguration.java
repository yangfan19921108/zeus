package com.fanxuankai.zeus.canal.client.redis.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.config.CanalWorker;
import com.fanxuankai.zeus.canal.client.core.flow.FlowOtter;
import com.fanxuankai.zeus.canal.client.core.flow.HandleSubscriber;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageHandler;
import com.fanxuankai.zeus.canal.client.redis.consumer.DeleteConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.EraseConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.InsertConsumer;
import com.fanxuankai.zeus.canal.client.redis.consumer.UpdateConsumer;
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
@Import({InsertConsumer.class, UpdateConsumer.class, DeleteConsumer.class, EraseConsumer.class})
@EnableConfigurationProperties(CanalRedisConfig.class)
@ConditionalOnProperty(value = CanalRedisConfig.ENABLED, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("rawtypes")
public class CanalWorkerAutoConfiguration {

    public static final String BEHAVIOR = "Redis";

    private static final String CONSUMER_INFO_NAME = "redisConsumerInfo";
    private static final String MESSAGE_HANDLER_NAME = "redisMessageHandler";
    private static final String FLOW_OTTER_NAME = "redisFlowOtter";

    @Resource
    private CanalConfig canalConfig;
    @Resource(name = CONSUMER_INFO_NAME)
    private ConsumerInfo consumerInfo;
    @Resource(name = MESSAGE_HANDLER_NAME)
    private MessageHandler messageHandler;
    @Resource(name = FLOW_OTTER_NAME)
    private FlowOtter flowOtter;

    @Bean(CONSUMER_INFO_NAME)
    public ConsumerInfo consumerInfo(InsertConsumer insertConsumer, UpdateConsumer updateConsumer,
                                     DeleteConsumer deleteConsumer, EraseConsumer eraseConsumer) {
        Map<CanalEntry.EventType, MessageConsumer> consumerMap = new HashMap<>(4);
        consumerMap.put(CanalEntry.EventType.INSERT, insertConsumer);
        consumerMap.put(CanalEntry.EventType.UPDATE, updateConsumer);
        consumerMap.put(CanalEntry.EventType.DELETE, deleteConsumer);
        consumerMap.put(CanalEntry.EventType.ERASE, eraseConsumer);
        return new ConsumerInfo(consumerMap, new ApplicationInfo(canalConfig.getApplicationName(),
                BEHAVIOR));
    }

    @Bean(MESSAGE_HANDLER_NAME)
    public MessageHandler messageHandler() {
        return new MessageHandler(consumerInfo);
    }

    @Bean(FLOW_OTTER_NAME)
    public FlowOtter flowOtter(CanalRedisConfig canalRedisConfig) {
        ApplicationInfo applicationInfo = new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR);
        HandleSubscriber.Config config = new HandleSubscriber.Config(messageHandler,
                applicationInfo, false);
        ConnectConfig connectConfig = new ConnectConfig(canalRedisConfig.getInstance(),
                RedisRepositoryScanner.INTERFACE_BEAN_SCANNER.getFilter(), applicationInfo);
        return new FlowOtter(FlowOtter.Config.builder()
                .canalConfig(canalConfig)
                .connectConfig(connectConfig)
                .consumerInfo(consumerInfo)
                .hsConfig(config)
                .build());
    }

    @Bean("redisCanalWorker")
    public CanalWorker canalWorker() {
        CanalWorker.Config config = new CanalWorker.Config(flowOtter,
                new ApplicationInfo(canalConfig.getApplicationName(), BEHAVIOR));
        return new CanalWorker(config);
    }
}

package com.fanxuankai.zeus.mq.broker.config;

import com.fanxuankai.zeus.mq.broker.core.EventListenerStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = MqBrokerProperties.PREFIX)
@Data
public class MqBrokerProperties {
    public static final String PREFIX = "zeus.mq-broker";
    /**
     * 最大重试次数
     */
    private Integer maxRetry = 6;
    /**
     * 生产频率 ms
     */
    private Long produceIntervalMillis = 1000L;
    /**
     * 消费频率 ms
     */
    private Long consumeIntervalMillis = 1000L;
    /**
     * 生产批次数量
     */
    private Long produceBatchCount = 100L;
    /**
     * 消费批次数量
     */
    private Long consumeBatchCount = 100L;
    /**
     * key: 队列名 value: EventListenerStrategy
     */
    private Map<String, EventListenerStrategy> eventListenerStrategy = Collections.emptyMap();
}

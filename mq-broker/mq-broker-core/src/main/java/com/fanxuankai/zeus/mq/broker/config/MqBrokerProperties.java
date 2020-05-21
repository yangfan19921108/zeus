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
     * 工作线程睡眠时间 ms
     */
    private Long intervalMillis = 1000L;
    /**
     * 抓取数据的数量
     */
    private Long batchCount = 100L;
    /**
     * key: 队列名 value: EventListenerStrategy
     */
    private Map<String, EventListenerStrategy> eventListenerStrategy = Collections.emptyMap();
}

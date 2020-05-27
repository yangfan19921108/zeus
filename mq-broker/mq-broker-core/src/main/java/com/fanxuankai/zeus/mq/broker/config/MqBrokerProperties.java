package com.fanxuankai.zeus.mq.broker.config;

import com.fanxuankai.zeus.mq.broker.core.consume.EventStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = MqBrokerProperties.PREFIX)
@Data
public class MqBrokerProperties {
    public static final String PREFIX = "zeus.mq-broker";
    /**
     * 最大并发数
     */
    private int maxConcurrent = Runtime.getRuntime().availableProcessors();
    /**
     * 拉取消息的数量
     */
    private int msgSize = 1000;
    /**
     * 事件注册, 如果有 EventListener 则不需要, 会自动注册事件
     */
    private Set<String> events = Collections.emptySet();
    /**
     * 最大重试次数
     */
    private int maxRetry = 3;
    /**
     * 拉取数据的间隔 ms
     */
    private long intervalMillis = 1_000;
    /**
     * 发布回调超时 ms
     */
    private long publisherCallbackTimeout = 20_000;
    /**
     * 消费超时 ms
     */
    private long consumeTimeout = 60_000;
    /**
     * key: 事件名 value: EventStrategy
     */
    private Map<String, EventStrategy> eventStrategy = Collections.emptyMap();
    /**
     * 数据库方言
     */
    private String dialectType = "mysql";
}

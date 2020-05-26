package com.fanxuankai.zeus.mq.broker.config;

import com.fanxuankai.zeus.mq.broker.core.consume.EventStrategy;
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
    private int maxRetry = 3;
    /**
     * 拉取数据的间隔 ms
     */
    private long intervalMillis = 1_000;
    /**
     * 拉取发送消息的数量
     */
    private long msgSendPullDataCount = 1_000;
    /**
     * 拉取接收消息的数量
     */
    private long msgReceivePullDataCount = 500;
    /**
     * 拉取发送消息分布式锁超时时间 ms
     */
    private long msgSendLockTimeout = 30_000;
    /**
     * 拉取接收消息分布式锁超时时间 ms
     */
    private long msgReceiveLockTimeout = 60_000;
    /**
     * 发布回调超时 ms
     */
    private long publisherCallbackTimeout = 20_000;
    /**
     * 发布回调超时分布式锁超时时间 ms
     */
    private long publisherCallbackLockTimeout = 300_000;
    /**
     * key: 事件名 value: EventStrategy
     */
    private Map<String, EventStrategy> eventStrategy = Collections.emptyMap();
    /**
     * 数据库方言
     */
    private String dialectType = "mysql";
}

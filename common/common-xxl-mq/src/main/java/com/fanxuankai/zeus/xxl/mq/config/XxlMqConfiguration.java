package com.fanxuankai.zeus.xxl.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fanxuankai
 */
@Data
@ConfigurationProperties(prefix = "xxl.mq.admin")
public class XxlMqConfiguration {
    private String address;
}

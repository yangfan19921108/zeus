package com.fanxuankai.zeus.common.xxl.mq.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = "xxl.mq.admin")
@Getter
@Setter
public class XxlMqConfiguration {
    private String address;
}

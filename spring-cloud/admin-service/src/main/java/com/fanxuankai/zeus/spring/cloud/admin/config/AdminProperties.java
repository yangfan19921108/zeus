package com.fanxuankai.zeus.spring.cloud.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = "admin")
@Data
public class AdminProperties {
    private String username;
    private String password;
}

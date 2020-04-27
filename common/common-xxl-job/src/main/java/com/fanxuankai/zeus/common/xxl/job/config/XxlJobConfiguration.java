package com.fanxuankai.zeus.common.xxl.job.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
@Getter
@Setter
public class XxlJobConfiguration {

    private Executor executor;
    private Admin admin;
    private String accessToken;

    @Getter
    @Setter
    public static final class Admin {
        private String addresses;
    }

    @Getter
    @Setter
    public static final class Executor {
        private String appName;
        private String ip;
        private Integer port;
        private String logPath;
        private int logRetentionDays;
    }

}
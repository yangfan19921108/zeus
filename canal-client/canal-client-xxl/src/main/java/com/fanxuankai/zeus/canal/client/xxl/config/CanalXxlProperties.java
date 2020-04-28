package com.fanxuankai.zeus.canal.client.xxl.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalMqProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = CanalXxlProperties.PREFIX)
public class CanalXxlProperties extends CanalMqProperties {

    public static final String PREFIX = CanalConfig.PREFIX + ".xxl";

    public static final String ENABLE = PREFIX + "." + CanalMqProperties.ENABLED;

}

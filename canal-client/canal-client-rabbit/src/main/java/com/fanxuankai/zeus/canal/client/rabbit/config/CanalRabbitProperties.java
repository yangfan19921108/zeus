package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalMqProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalRabbitProperties.PREFIX)
public class CanalRabbitProperties extends CanalMqProperties {

    public static final String PREFIX = CanalConfig.PREFIX + ".rabbit";

    public static final String ENABLE = PREFIX + "." + CanalMqProperties.ENABLED;

}

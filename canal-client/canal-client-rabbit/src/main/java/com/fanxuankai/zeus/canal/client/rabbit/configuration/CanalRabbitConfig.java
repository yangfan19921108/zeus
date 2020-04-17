package com.fanxuankai.zeus.canal.client.rabbit.configuration;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalMqConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalRabbitConfig.PREFIX)
public class CanalRabbitConfig extends CanalMqConfig {

    public static final String PREFIX = CanalConfig.PREFIX + ".rabbit";

    public static final String ENABLE = PREFIX + "." + CanalMqConfig.ENABLED;

}

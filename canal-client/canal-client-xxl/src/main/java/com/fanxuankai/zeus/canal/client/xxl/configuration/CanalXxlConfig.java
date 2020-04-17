package com.fanxuankai.zeus.canal.client.xxl.configuration;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalMqConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalXxlConfig.PREFIX)
public class CanalXxlConfig extends CanalMqConfig {

    public static final String PREFIX = CanalConfig.PREFIX + ".xxl";

    public static final String ENABLE = PREFIX + "." + CanalMqConfig.ENABLED;

}

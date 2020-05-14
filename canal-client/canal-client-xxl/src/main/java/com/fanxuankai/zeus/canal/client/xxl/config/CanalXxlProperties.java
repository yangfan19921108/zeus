package com.fanxuankai.zeus.canal.client.xxl.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalMqProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalXxlProperties.PREFIX)
public class CanalXxlProperties extends CanalMqProperties {

    public static final String PREFIX = CanalProperties.PREFIX + ".xxl";

    public static final String ENABLE = PREFIX + "." + CanalMqProperties.ENABLED;

}

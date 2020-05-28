package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@Data
@ConfigurationProperties(prefix = CanalMqProperties.PREFIX)
public class CanalMqProperties {

    public static final String PREFIX = CanalProperties.PREFIX + ".mq";
    public static final String ENABLED = "enabled";

    /**
     * MQ 对应的 canal 实例名
     */
    private String instance = "example";

    /**
     * 跳过处理
     */
    private Boolean skip = Boolean.FALSE;

    /**
     * 是否激活
     */
    private Boolean enabled = Boolean.TRUE;
}

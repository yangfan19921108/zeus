package com.fanxuankai.zeus.canal.client.es.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalEsProperties.PREFIX)
@Getter
@Setter
public class CanalEsProperties {

    public static final String PREFIX = CanalProperties.PREFIX + ".es";
    public static final String ENABLED = PREFIX + ".enabled";

    /**
     * Es 对应的 canal 实例名
     */
    private String instance = "example";

    /**
     * 是否激活
     */
    private boolean enabled = true;
}

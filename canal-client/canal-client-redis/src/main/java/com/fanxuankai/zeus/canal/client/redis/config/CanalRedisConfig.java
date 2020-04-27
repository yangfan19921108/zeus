package com.fanxuankai.zeus.canal.client.redis.config;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@ConfigurationProperties(prefix = CanalRedisConfig.PREFIX)
@Getter
@Setter
public class CanalRedisConfig {

    public static final String PREFIX = CanalConfig.PREFIX + ".redis";
    public static final String ENABLED = PREFIX + ".enabled";

    /**
     * Redis 对应的 canal 实例名
     */
    private String instance = "example";

    /**
     * 是否激活
     */
    private boolean enabled = true;
}

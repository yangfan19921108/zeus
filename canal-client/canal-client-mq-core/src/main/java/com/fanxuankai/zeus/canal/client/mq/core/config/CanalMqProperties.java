package com.fanxuankai.zeus.canal.client.mq.core.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置
 *
 * @author fanxuankai
 */
@Getter
@Setter
public class CanalMqProperties {

    protected static final String ENABLED = "enabled";

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
    private boolean enabled = Boolean.TRUE;

}

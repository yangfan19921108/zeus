package com.fanxuankai.zeus.canal.client.core.model;

/**
 * Canal 链接配置文件
 *
 * @author fanxuankai
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectConfig {
    private final String instance;
    private final String filter;
    private final ApplicationInfo applicationInfo;
}

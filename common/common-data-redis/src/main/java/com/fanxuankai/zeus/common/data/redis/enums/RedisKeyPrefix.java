package com.fanxuankai.zeus.common.data.redis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxuankai
 */

@Getter
@AllArgsConstructor
public enum RedisKeyPrefix {
    // 数据库缓存
    DB_CACHE("zeus.canal.dbCache"),
    // 分布式锁
    LOCK("zeus.lock"),
    // 业务缓存
    SERVICE_CACHE("zeus.serviceCache"),
    ;
    private final String value;
}

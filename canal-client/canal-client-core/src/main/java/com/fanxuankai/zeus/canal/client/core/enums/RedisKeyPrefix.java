package com.fanxuankai.zeus.canal.client.core.enums;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxuankai
 */

@Getter
@AllArgsConstructor
public enum RedisKeyPrefix {
    // 数据库缓存
    DB_CACHE(String.format("%s%sDbCache", RedisConstants.GLOBAL_NAME, CommonConstants.SEPARATOR)),
    // 分布式锁
    LOCK(String.format("%s%sLock", RedisConstants.GLOBAL_NAME, CommonConstants.SEPARATOR)),
    // 业务缓存
    SERVICE_CACHE(String.format("%s%sServiceCache", RedisConstants.GLOBAL_NAME, CommonConstants.SEPARATOR)),
    ;
    private final String value;
}

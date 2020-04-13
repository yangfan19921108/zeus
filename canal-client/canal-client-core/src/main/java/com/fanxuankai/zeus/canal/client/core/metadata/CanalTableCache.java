package com.fanxuankai.zeus.canal.client.core.metadata;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CanalTable 注解元数据缓存
 *
 * @author fanxuankai
 */
public class CanalTableCache {
    private static final Map<String, CanalTableMetadata> DATA_BY_TABLE = new ConcurrentHashMap<>();

    public static CanalTableMetadata getMetadata(EntryWrapper entryWrapper) {
        return DATA_BY_TABLE.get(fullTableName(entryWrapper.getSchemaName(), entryWrapper.getTableName()));
    }

    public static void put(CanalTableMetadata metadata) {
        DATA_BY_TABLE.put(fullTableName(metadata.getSchema(), metadata.getName()), metadata);
    }

    private static String fullTableName(String schema, String name) {
        return schema + CommonConstants.SEPARATOR + name;
    }
}

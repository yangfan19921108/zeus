package com.fanxuankai.zeus.canal.client.core.util;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.common.data.redis.enums.RedisKeyPrefix;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis 工具类
 *
 * @author fanxuankai
 */
public class RedisUtils {

    /**
     * 生成 key
     *
     * @param schema 数据库名
     * @param table  表名
     * @return 生成默认的 key
     */
    public static String key(String schema, String table) {
        return key(schema, table, null);
    }

    /**
     * 生成 key
     *
     * @param schema 数据库名
     * @param table  表名
     * @param suffix 后缀
     * @return 生成默认的 key
     */
    public static String key(String schema, String table, String suffix) {
        String key =
                RedisKeyPrefix.DB_CACHE.getValue() + CommonConstants.SEPARATOR + schema + CommonConstants.SEPARATOR + table;
        if (StringUtils.isNotEmpty(suffix)) {
            return key + CommonConstants.SEPARATOR + suffix;
        }
        return key;
    }

    /**
     * 生成 key
     *
     * @param key    自定义的key
     * @param suffix 后缀
     * @return 生成自定义的 key
     */
    public static String customKey(String key, String suffix) {
        return key + CommonConstants.SEPARATOR + suffix;
    }

    /**
     * 生成 key
     *
     * @param prefix 后缀
     * @param custom 自定义
     * @return 生成自定义的 key
     */
    public static String customKey(RedisKeyPrefix prefix, String custom) {
        return prefix.getValue() + CommonConstants.SEPARATOR + custom;
    }

    /**
     * 生成哈希表的 key 的后缀
     *
     * @param columnList 数据库列名
     * @return column0:column1:column2
     */
    public static String keySuffix(List<String> columnList) {
        return String.join(CommonConstants.SEPARATOR1, columnList);
    }

    /**
     * 生成哈希表的 hashKey
     *
     * @param columnList 数据库列名
     * @param columnMap  数据库列键值对
     * @return (columnValue0):(columnValue1):(columnValue2)
     */
    public static String combineHashKey(List<String> columnList, Map<String, String> columnMap) {
        return columnList.stream()
                .map(columnMap::get)
                .map(s -> "(" + s + ")")
                .collect(Collectors.joining(CommonConstants.SEPARATOR1));
    }
}

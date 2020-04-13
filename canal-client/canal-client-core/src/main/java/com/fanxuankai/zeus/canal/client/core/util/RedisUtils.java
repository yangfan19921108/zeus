package com.fanxuankai.zeus.canal.client.core.util;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.enums.RedisKeyPrefix;
import org.apache.commons.lang.StringUtils;

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
}

package com.fanxuankai.zeus.canal.client.core.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.QueuePrefixConstants;

/**
 * MQ 队列名工具类
 *
 * @author fanxuankai
 */
public class QueueNameUtils {
    public static String name(String schema, String table) {
        return QueuePrefixConstants.CANAL_2_MQ + CommonConstants.SEPARATOR + schema + CommonConstants.SEPARATOR + table;
    }

    public static String name(String schema, String table, CanalEntry.EventType eventType) {
        return QueuePrefixConstants.CANAL_2_MQ + CommonConstants.SEPARATOR + schema + CommonConstants.SEPARATOR + table + CommonConstants.SEPARATOR + eventType;
    }

    public static String customName(String queue, CanalEntry.EventType eventType) {
        return QueuePrefixConstants.CANAL_2_MQ + CommonConstants.SEPARATOR + queue + CommonConstants.SEPARATOR + eventType;
    }
}

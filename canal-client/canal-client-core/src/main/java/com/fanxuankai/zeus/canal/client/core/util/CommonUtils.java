package com.fanxuankai.zeus.canal.client.core.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公共工具类
 *
 * @author fanxuankai
 */
public class CommonUtils {

    /**
     * 所有数据列转为 Map
     *
     * @param columnList 每一列
     * @return HashMap
     */
    public static Map<String, String> toMap(List<CanalEntry.Column> columnList) {
        return columnList.stream()
                .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
    }

    /**
     * 所有数据列转为 Map
     *
     * @param columnList 每一列
     * @return HashMap
     */
    public static Map<String, CanalEntry.Column> toColumnMap(List<CanalEntry.Column> columnList) {
        return columnList.stream()
                .collect(Collectors.toMap(CanalEntry.Column::getName, o -> o));
    }

    public static String fullTableName(String schema, String table) {
        return schema + CommonConstants.SEPARATOR + table;
    }
}

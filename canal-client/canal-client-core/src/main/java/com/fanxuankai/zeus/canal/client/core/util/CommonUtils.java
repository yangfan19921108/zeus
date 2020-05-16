package com.fanxuankai.zeus.canal.client.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
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
     * 转 MD5
     *
     * @param columns 数据行
     * @return md5
     */
    public static String md5(List<CanalEntry.Column> columns) {
        JSONObject jsonObject = new JSONObject(columns.size(), true);
        columns.forEach(column -> jsonObject.put(column.getName(), column.getValue()));
        String jsonString = jsonObject.toString();
        return DigestUtils.md5Hex(jsonString);
    }

    /**
     * 转 JSON 字符串
     *
     * @param columnList 数据行
     * @return { columnName: columnValue }
     */
    public static String json(List<CanalEntry.Column> columnList) {
        return JSON.toJSONString(CommonUtils.toMap(columnList));
    }

    /**
     * 转 JSON 字符串
     *
     * @param beforeColumns 旧的数据行
     * @param afterColumns  新的数据行
     * @return [ { columnName: columnValue }, { columnName: columnValue }]
     */
    public static String json(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        Map<String, String> map0 = CommonUtils.toMap(beforeColumns);
        Map<String, String> map1 = CommonUtils.toMap(afterColumns);
        List<Object> list = new ArrayList<>(2);
        list.add(map0);
        list.add(map1);
        return new JSONArray(list).toJSONString();
    }

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

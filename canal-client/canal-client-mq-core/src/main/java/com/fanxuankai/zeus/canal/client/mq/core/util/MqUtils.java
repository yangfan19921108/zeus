package com.fanxuankai.zeus.canal.client.mq.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.QueueNameUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner.INTERFACE_BEAN_SCANNER;

/**
 * @author fanxuankai
 */
public class MqUtils {
    /**
     * 生成消息主题
     *
     * @param entryWrapper 数据
     * @param eventType    事件类型
     * @return prefix.schema.table.eventType
     */
    public static String routingKey(EntryWrapper entryWrapper, CanalEntry.EventType eventType) {
        CanalToMqMetadata metadata = INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        if (StringUtils.isNotBlank(metadata.getName())) {
            return QueueNameUtils.customName(metadata.getName(), eventType);
        }
        Class<?> domainType =
                INTERFACE_BEAN_SCANNER.getDomainType(INTERFACE_BEAN_SCANNER.getInterfaceBeanClass(entryWrapper));
        CanalTableMetadata tableMetadata = INTERFACE_BEAN_SCANNER.getCanalTableMetadata(domainType);
        return QueueNameUtils.name(tableMetadata.getSchema(), tableMetadata.getName(), eventType);
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
}

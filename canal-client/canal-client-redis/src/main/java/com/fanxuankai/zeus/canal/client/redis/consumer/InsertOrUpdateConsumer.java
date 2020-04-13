package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.annotation.CombineKey;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.config.RedisRepositoryScanner;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import com.google.common.collect.Maps;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新增或者修改事件消费者
 *
 * @author fanxuankai
 */
public class InsertOrUpdateConsumer extends AbstractRedisConsumer<Map<String, Map<String, Object>>> {

    @Override
    public Map<String, Map<String, Object>> process(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata =
                RedisRepositoryScanner.INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        List<String> keys = canalToRedisMetadata.getKeys();
        boolean idAsField = canalToRedisMetadata.isIdAsField();
        List<CombineKey> combineKeys = canalToRedisMetadata.getCombineKeys();
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        String key = keyOf(entryWrapper);
        entryWrapper.getAllRowDataList().forEach(rowData -> {
            Map<String, String> value = CommonUtils.toMap(rowData.getAfterColumnsList());
            rowData.getAfterColumnsList()
                    .stream()
                    .filter(column -> {
                        if (idAsField && column.getIsKey()) {
                            return true;
                        }
                        if (CollectionUtils.isEmpty(keys)) {
                            return true;
                        }
                        return keys.contains(column.getName());
                    })
                    .forEach(column -> {
                        if (column.getIsKey()) {
                            map.computeIfAbsent(key, s -> Maps.newHashMap()).put(column.getValue(), value);
                        } else if (keys.contains(column.getName())) {
                            map.computeIfAbsent(keyOf(entryWrapper, column.getName()),
                                    s -> Maps.newHashMap()).put(column.getValue(), value);
                        }
                    });
            if (!CollectionUtils.isEmpty(combineKeys)) {
                Map<String, String> columnMap = CommonUtils.toMap(rowData.getAfterColumnsList());
                for (CombineKey combineKey : combineKeys) {
                    List<String> columnList = Arrays.asList(combineKey.values());
                    String suffix = String.join(CommonConstants.SEPARATOR1, columnList);
                    String name =
                            columnList.stream().map(columnMap::get).collect(Collectors.joining(CommonConstants.SEPARATOR1));
                    map.computeIfAbsent(keyOf(entryWrapper, suffix), s -> Maps.newHashMap())
                            .put(name, value);
                }
            }
        });
        return map;
    }

    @Override
    public void consume(Map<String, Map<String, Object>> stringMapMap) {
        if (!stringMapMap.isEmpty()) {
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            stringMapMap.forEach(hash::putAll);
        }
    }
}

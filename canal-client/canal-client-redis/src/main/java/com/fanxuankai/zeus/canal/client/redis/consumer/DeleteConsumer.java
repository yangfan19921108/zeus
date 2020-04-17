package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.configuration.RedisRepositoryScanner;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import com.google.common.collect.Maps;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractRedisConsumer<Map<String, List<String>>> {

    @Override
    public Map<String, List<String>> process(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata =
                RedisRepositoryScanner.INTERFACE_BEAN_SCANNER.getMetadata(entryWrapper);
        List<String> keys = canalToRedisMetadata.getKeys();
        List<List<String>> combineKeys = canalToRedisMetadata.getCombineKeys();
        String key = keyOf(entryWrapper);
        Map<String, List<String>> hash = Maps.newHashMap();
        entryWrapper.getAllRowDataList()
                .forEach(rowData -> {
                    rowData.getBeforeColumnsList()
                            .stream()
                            .filter(column -> {
                                if (CollectionUtils.isEmpty(keys)) {
                                    return true;
                                }
                                return keys.contains(column.getName());
                            })
                            .forEach(o -> {
                                if (o.getIsKey()) {
                                    hash.computeIfAbsent(key, s -> new ArrayList<>()).add(o.getValue());
                                } else if (keys.contains(o.getName())) {
                                    hash.computeIfAbsent(keyOf(entryWrapper, o.getName()),
                                            s -> new ArrayList<>()).add(o.getValue());
                                }
                            });
                    if (!CollectionUtils.isEmpty(combineKeys)) {
                        Map<String, String> columnMap = CommonUtils.toMap(rowData.getBeforeColumnsList());
                        for (List<String> columnList : combineKeys) {
                            String keySuffix = RedisUtils.keySuffix(columnList);
                            String name = RedisUtils.combineHashKey(columnList, columnMap);
                            hash.computeIfAbsent(keyOf(entryWrapper, keySuffix), s -> new ArrayList<>()).add(name);
                        }
                    }
                });
        return hash;
    }

    @Override
    public boolean filterable() {
        return false;
    }

    @Override
    public void consume(Map<String, List<String>> hash) {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        hash.forEach((s, strings) -> {
            Object[] objects = strings.toArray();
            ops.delete(s, objects);
        });
    }
}

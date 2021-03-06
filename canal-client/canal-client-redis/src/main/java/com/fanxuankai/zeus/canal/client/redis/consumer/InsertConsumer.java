package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import com.fanxuankai.zeus.canal.client.redis.util.RedisKeyGenerator;
import com.google.common.collect.Maps;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 新增事件消费者
 *
 * @author fanxuankai
 */
public class InsertConsumer extends AbstractRedisConsumer<Map<String, Map<String, Object>>> {

    @Override
    public Map<String, Map<String, Object>> process(EntryWrapper entryWrapper) {
        CanalToRedisMetadata canalToRedisMetadata = getMetadata(entryWrapper);
        assert canalToRedisMetadata != null;
        List<String> keys = canalToRedisMetadata.getKeys();
        boolean idAsHashKey = canalToRedisMetadata.isIdAsHashKey();
        List<List<String>> combineKeys = canalToRedisMetadata.getCombineKeys();
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        String key = RedisKeyGenerator.keyOf(entryWrapper);
        entryWrapper.getAllRowDataList().forEach(rowData -> {
            Map<String, String> hashValue = CommonUtils.toMap(rowData.getAfterColumnsList());
            rowData.getAfterColumnsList()
                    .stream()
                    .filter(column -> {
                        if (idAsHashKey && column.getIsKey()) {
                            return true;
                        }
                        if (CollectionUtils.isEmpty(keys)) {
                            return true;
                        }
                        return keys.contains(column.getName());
                    })
                    .forEach(column -> {
                        if (column.getIsKey()) {
                            map.computeIfAbsent(key, s -> Maps.newHashMap()).put(column.getValue(), hashValue);
                        } else if (keys.contains(column.getName())) {
                            map.computeIfAbsent(RedisKeyGenerator.keyOf(entryWrapper, column.getName()),
                                    s -> Maps.newHashMap()).put(column.getValue(), hashValue);
                        }
                    });
            if (!CollectionUtils.isEmpty(combineKeys)) {
                for (List<String> columnList : combineKeys) {
                    String keySuffix = RedisUtils.keySuffix(columnList);
                    String hashKey = RedisUtils.combineHashKey(columnList, hashValue);
                    map.computeIfAbsent(RedisKeyGenerator.keyOf(entryWrapper, keySuffix), s -> Maps.newHashMap())
                            .put(hashKey, hashValue);
                }
            }
        });
        return map;
    }

    @Override
    public void consume(Map<String, Map<String, Object>> hash) {
        if (hash.isEmpty()) {
            return;
        }
        HashOperations<Object, Object, Object> opsForHash = redisTemplate.opsForHash();
        redisTemplate.execute((RedisConnection rc) -> {
            hash.forEach(opsForHash::putAll);
            return null;
        });
    }
}

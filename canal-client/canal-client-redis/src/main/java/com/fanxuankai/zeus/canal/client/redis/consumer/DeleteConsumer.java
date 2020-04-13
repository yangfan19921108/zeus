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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<CombineKey> combineKeys = canalToRedisMetadata.getCombineKeys();
        String key = keyOf(entryWrapper);
        Map<String, List<String>> hashKeyMap = Maps.newHashMap();
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
                                    hashKeyMap.computeIfAbsent(key, s -> new ArrayList<>()).add(o.getValue());
                                } else if (keys.contains(o.getName())) {
                                    hashKeyMap.computeIfAbsent(keyOf(entryWrapper, o.getName()),
                                            s -> new ArrayList<>()).add(o.getValue());
                                }
                            });
                    if (!CollectionUtils.isEmpty(combineKeys)) {
                        Map<String, String> columnMap = CommonUtils.toMap(rowData.getBeforeColumnsList());
                        for (CombineKey combineKey : combineKeys) {
                            List<String> columnList = Arrays.asList(combineKey.values());
                            String suffix = String.join(CommonConstants.SEPARATOR1, columnList);
                            String name =
                                    columnList.stream().map(columnMap::get).collect(Collectors.joining(CommonConstants.SEPARATOR1));
                            hashKeyMap.computeIfAbsent(keyOf(entryWrapper, suffix), s -> new ArrayList<>()).add(name);
                        }
                    }
                });
        return hashKeyMap;
    }

    @Override
    public void consume(Map<String, List<String>> stringListMap) {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        stringListMap.forEach((s, strings) -> {
            Object[] objects = strings.toArray();
            ops.delete(s, objects);
        });
    }
}

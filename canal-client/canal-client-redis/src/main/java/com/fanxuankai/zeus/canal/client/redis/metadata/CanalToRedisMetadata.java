package com.fanxuankai.zeus.canal.client.redis.metadata;

import com.fanxuankai.zeus.canal.client.core.metadata.CombineKeyMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.KeyMetadata;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CanalToRedis 注解元数据
 *
 * @author fanxuankai
 */
@Getter
public class CanalToRedisMetadata {
    private String key = "";
    private boolean idAsHashKey;
    private List<String> keys = Collections.emptyList();
    private List<List<String>> combineKeys = Collections.emptyList();
    private FilterMetadata filterMetadata = new FilterMetadata();

    public CanalToRedisMetadata(CanalToRedis canalToRedis) {
        if (canalToRedis != null) {
            this.key = canalToRedis.key();
            this.idAsHashKey = canalToRedis.idAsHashKey();
            this.keys = Arrays.stream(canalToRedis.uniqueKeys())
                    .map(KeyMetadata::new)
                    .map(KeyMetadata::getValue)
                    .collect(Collectors.toList());
            this.combineKeys =
                    Arrays.stream(canalToRedis.combineKeys())
                            .map(CombineKeyMetadata::new)
                            .map(CombineKeyMetadata::getValues)
                            .collect(Collectors.toList());
            this.filterMetadata = new FilterMetadata(canalToRedis.filter());
        }
    }
}

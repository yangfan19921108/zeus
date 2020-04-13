package com.fanxuankai.zeus.canal.client.redis.metadata;

import com.fanxuankai.zeus.canal.client.core.annotation.CombineKey;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CanalToRedis 注解元数据
 *
 * @author fanxuankai
 */
@Getter
public class CanalToRedisMetadata {
    private String key = "";
    private boolean idAsField;
    private List<String> keys = Collections.emptyList();
    private List<CombineKey> combineKeys = Collections.emptyList();
    private FilterMetadata filterMetadata = new FilterMetadata();

    public CanalToRedisMetadata(CanalToRedis canalToRedis) {
        if (canalToRedis != null) {
            this.key = canalToRedis.key();
            this.idAsField = canalToRedis.idAsField();
            this.keys = Arrays.asList(canalToRedis.uniqueKeys());
            this.combineKeys = Arrays.asList(canalToRedis.combineKeys());
            this.filterMetadata = new FilterMetadata(canalToRedis.filter());
        }
    }

}

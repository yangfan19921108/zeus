package com.fanxuankai.zeus.canal.client.es.metadata;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.es.annotation.CanalToEs;
import lombok.Getter;

/**
 * CanalToEs 注解元数据
 *
 * @author fanxuankai
 */
@Getter
public class CanalToEsMetadata {
    private FilterMetadata filterMetadata = new FilterMetadata();

    public CanalToEsMetadata(CanalToEs canalToEs) {
        if (canalToEs != null) {
            this.filterMetadata = new FilterMetadata(canalToEs.filter());
        }
    }
}

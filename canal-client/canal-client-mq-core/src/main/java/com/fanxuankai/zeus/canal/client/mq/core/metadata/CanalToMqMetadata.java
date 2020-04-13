package com.fanxuankai.zeus.canal.client.mq.core.metadata;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CanalToMq 注解元数据
 *
 * @author fanxuankai
 */
@Getter
public class CanalToMqMetadata {
    private String name = "";
    private List<CanalEntry.EventType> eventTypes = Collections.emptyList();
    private FilterMetadata filterMetadata = new FilterMetadata();

    public CanalToMqMetadata(CanalToMq canalToMq) {
        if (canalToMq != null) {
            this.name = canalToMq.name();
            this.eventTypes = Arrays.asList(canalToMq.eventTypes());
            this.filterMetadata = new FilterMetadata(canalToMq.filter());
        }
    }
}

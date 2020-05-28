package com.fanxuankai.zeus.canal.client.mq.core.metadata;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.alibaba.otter.canal.protocol.CanalEntry.EventType.*;

/**
 * CanalToMq 注解元数据
 *
 * @author fanxuankai
 */
@Getter
public class CanalToMqMetadata {
    private String name = "";
    private List<CanalEntry.EventType> eventTypes = Arrays.asList(INSERT, DELETE, UPDATE);
    private FilterMetadata filterMetadata = new FilterMetadata();
    private boolean repeatableConsumption;

    public CanalToMqMetadata(CanalToMq canalToMq) {
        if (canalToMq != null) {
            this.name = canalToMq.name();
            this.eventTypes = Arrays.asList(canalToMq.eventTypes());
            this.filterMetadata = new FilterMetadata(canalToMq.filter());
            this.repeatableConsumption = canalToMq.repeatableConsumption();
        }
    }
}

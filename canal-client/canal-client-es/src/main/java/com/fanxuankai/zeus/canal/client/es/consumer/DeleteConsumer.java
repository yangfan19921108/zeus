package com.fanxuankai.zeus.canal.client.es.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.es.config.CanalToEsScanner;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 删除事件消费者
 *
 * @author fanxuankai
 */
public class DeleteConsumer extends AbstractEsConsumer<DeleteConsumer.ProcessData> {

    @Override
    public ProcessData process(EntryWrapper entryWrapper) {
        Set<String> ids = entryWrapper.getAllRowDataList()
                .stream()
                .map(CanalEntry.RowData::getBeforeColumnsList)
                .map(columns ->
                        columns.stream()
                                .filter(CanalEntry.Column::getIsKey)
                                .findFirst()
                                .orElse(null))
                .filter(Objects::nonNull)
                .map(CanalEntry.Column::getValue)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        DeleteQuery deleteQuery = new DeleteQuery();
        Class<?> domainClass = CanalToEsScanner.CONSUME_CONFIGURATION.getDomain(entryWrapper);
        deleteQuery.setQuery(QueryBuilders.termsQuery("_id", ids.toArray(new String[0])));
        return ProcessData.builder()
                .domainClass(domainClass)
                .deleteQuery(deleteQuery)
                .build();
    }

    @Override
    public boolean filterable() {
        return false;
    }

    @Override
    public void consume(ProcessData processData) {
        if (processData == null) {
            return;
        }
        elasticsearchTemplate.delete(processData.getDeleteQuery(), processData.getDomainClass());
    }

    @Builder
    @Getter
    public static class ProcessData {
        private final Class<?> domainClass;
        private final DeleteQuery deleteQuery;
    }
}

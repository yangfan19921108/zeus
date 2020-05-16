package com.fanxuankai.zeus.canal.client.es.consumer;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.DomainConverter;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.es.config.CanalToEsScanner;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 新增事件消费者
 *
 * @author fanxuankai
 */
public class InsertConsumer extends AbstractEsConsumer<List<IndexQuery>> {

    public InsertConsumer(ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticsearchTemplate);
    }

    @Override
    public List<IndexQuery> process(EntryWrapper entryWrapper) {
        Class<?> domainClass = CanalToEsScanner.CONSUME_CONFIGURATION.getDomain(entryWrapper);
        return entryWrapper.getAllRowDataList()
                .stream()
                .map(rowData -> createIndexQuery(rowData, domainClass))
                .collect(Collectors.toList());
    }

    @Override
    public void consume(List<IndexQuery> queries) {
        if (CollectionUtils.isEmpty(queries)) {
            return;
        }
        elasticsearchTemplate.bulkIndex(queries);
    }

    private IndexQuery createIndexQuery(CanalEntry.RowData rowData, Class<?> domainClass) {
        IndexQuery query = new IndexQuery();
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        query.setObject(DomainConverter.of(CommonUtils.json(afterColumnsList), domainClass));
        afterColumnsList.stream()
                .filter(CanalEntry.Column::getIsKey)
                .findFirst()
                .ifPresent(column -> query.setId(column.getValue()));
        return query;
    }
}

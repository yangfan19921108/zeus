package com.fanxuankai.zeus.canal.client.es.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.es.metadata.CanalToEsMetadata;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import static com.fanxuankai.zeus.canal.client.es.config.CanalToEsScanner.CONSUME_CONFIGURATION;

/**
 * Es 抽象消费者
 *
 * @author fanxuankai
 */
public abstract class AbstractEsConsumer<R> implements MessageConsumer<R> {

    protected final ElasticsearchTemplate elasticsearchTemplate;

    public AbstractEsConsumer(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getAnnotation(entryWrapper) != null;
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return getMetadata(entryWrapper).getFilterMetadata();
    }

    @Override
    public Class<?> domainClass(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getDomain(entryWrapper);
    }

    protected CanalToEsMetadata getMetadata(EntryWrapper entryWrapper) {
        CanalToEsMetadata canalToEsMetadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        assert canalToEsMetadata != null;
        return canalToEsMetadata;
    }

}

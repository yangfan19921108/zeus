package com.fanxuankai.zeus.canal.client.es.consumer;

import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.es.config.CanalToEsScanner;

/**
 * 删表事件消费者
 *
 * @author fanxuankai
 */
public class EraseConsumer extends AbstractEsConsumer<Class<?>> {

    @Override
    public Class<?> process(EntryWrapper entryWrapper) {
        return CanalToEsScanner.CONSUME_CONFIGURATION.getDomain(entryWrapper);
    }

    @Override
    public boolean filterable() {
        return false;
    }

    @Override
    public void consume(Class<?> domainClass) {
        elasticsearchTemplate.deleteIndex(domainClass);
    }
}

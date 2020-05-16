package com.fanxuankai.zeus.canal.client.core.protocol;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;

/**
 * Message 消费者
 *
 * @author fanxuankai
 */
public interface MessageConsumer<R> extends Consumer<EntryWrapper, R> {

    /**
     * 过滤
     *
     * @param entryWrapper 数据
     * @return Filter 注解元数据
     */
    FilterMetadata filter(EntryWrapper entryWrapper);

    /**
     * 实体类
     *
     * @param entryWrapper 数据
     * @return 数据库实体类
     */
    Class<?> domainClass(EntryWrapper entryWrapper);

}

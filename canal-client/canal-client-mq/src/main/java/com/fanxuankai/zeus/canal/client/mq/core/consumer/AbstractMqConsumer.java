package com.fanxuankai.zeus.canal.client.mq.core.consumer;

import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner.CONSUME_CONFIGURATION;

/**
 * MQ 抽象消费者
 *
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractMqConsumer implements MessageConsumer<List<Event>> {

    private final EventPublisher eventPublisher;

    public AbstractMqConsumer(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean canProcess(EntryWrapper entryWrapper) {
        if (CONSUME_CONFIGURATION.getAnnotation(entryWrapper) == null) {
            return false;
        }
        CanalToMqMetadata metadata = CONSUME_CONFIGURATION.getMetadata(entryWrapper);
        return metadata == null || metadata.getEventTypes().contains(entryWrapper.getEventType());
    }

    @Override
    public FilterMetadata filter(EntryWrapper entryWrapper) {
        return Objects.requireNonNull(CONSUME_CONFIGURATION.getMetadata(entryWrapper)).getFilterMetadata();
    }

    @Override
    public Class<?> domainClass(EntryWrapper entryWrapper) {
        return CONSUME_CONFIGURATION.getDomain(entryWrapper);
    }

    @Override
    public void consume(List<Event> events) {
        eventPublisher.publish(events);
    }
}

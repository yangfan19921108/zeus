package com.fanxuankai.zeus.mq.broker;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventPublisher;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.enums.MessageType;
import com.fanxuankai.zeus.mq.broker.mapper.MqBrokerMessageMapper;
import com.fanxuankai.zeus.mq.broker.service.MqBrokerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class PersistenceEventPublisher implements EventPublisher {

    @Resource
    private MqBrokerMessageMapper mqBrokerMessageMapper;
    @Resource
    private MqBrokerMessageService mqBrokerMessageService;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public void publish(List<Event> events) {
        try {
            mqBrokerMessageService.saveBatch(events.stream()
                    .map(this::createMessageSend)
                    .collect(Collectors.toList()));
        } catch (DuplicateKeyException e) {
            log.info("生产端防重");
        }
    }

    @Override
    public void publish(Event event) {
        try {
            mqBrokerMessageMapper.insert(createMessageSend(event));
        } catch (DuplicateKeyException e) {
            log.info("生产端防重, name: {} key: {} data: {}", event.getName(), event.getKey(), event.getData());
        }
    }

    private MqBrokerMessage createMessageSend(Event event) {
        MqBrokerMessage message = new MqBrokerMessage();
        message.setId(idGenerator.generateId().longValue());
        message.setType(MessageType.SEND.getCode());
        message.setQueue(event.getName());
        message.setCode(event.getKey());
        message.setContent(event.getData());
        message.setStatus(Status.CREATED.getCode());
        message.setRetry(0);
        message.setCreateDate(LocalDateTime.now());
        message.setLastModifiedDate(LocalDateTime.now());
        return message;
    }
}

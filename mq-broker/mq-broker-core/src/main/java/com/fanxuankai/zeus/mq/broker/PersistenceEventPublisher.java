package com.fanxuankai.zeus.mq.broker;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventPublisher;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;
import com.fanxuankai.zeus.mq.broker.mapper.MessageSendMapper;
import com.fanxuankai.zeus.mq.broker.service.MessageSendService;
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
    private MessageSendMapper messageSendMapper;
    @Resource
    private MessageSendService messageSendService;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public void publish(List<Event> events) {
        try {
            messageSendService.saveBatch(events.stream()
                    .map(this::createMessageSend)
                    .collect(Collectors.toList()));
        } catch (DuplicateKeyException e) {
            log.info("生产端防重");
        }
    }

    @Override
    public void publish(Event event) {
        try {
            messageSendMapper.insert(createMessageSend(event));
        } catch (DuplicateKeyException e) {
            log.info("生产端防重, name: {} key: {} data: {}", event.getName(), event.getKey(), event.getData());
        }
    }

    private MessageSend createMessageSend(Event event) {
        MessageSend messageSend = new MessageSend();
        messageSend.setId(idGenerator.generateId().longValue());
        messageSend.setQueue(event.getName());
        messageSend.setCode(event.getKey());
        messageSend.setContent(event.getData());
        messageSend.setStatus(Status.CREATED.getCode());
        messageSend.setRetry(0);
        messageSend.setCreateDate(LocalDateTime.now());
        messageSend.setLastModifiedDate(LocalDateTime.now());
        return messageSend;
    }
}

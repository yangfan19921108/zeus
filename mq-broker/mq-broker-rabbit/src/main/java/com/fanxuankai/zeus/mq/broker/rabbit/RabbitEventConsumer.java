package com.fanxuankai.zeus.mq.broker.rabbit;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventConsumer;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.enums.MessageType;
import com.fanxuankai.zeus.mq.broker.mapper.MqBrokerMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
@Slf4j
public class RabbitEventConsumer implements EventConsumer {

    @Resource
    private MqBrokerMessageMapper mqBrokerMessageMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public void accept(Event event) {
        MqBrokerMessage message = new MqBrokerMessage();
        message.setId(idGenerator.generateId().longValue());
        message.setType(MessageType.RECEIVE.getCode());
        message.setCode(event.getKey());
        message.setQueue(event.getName());
        message.setContent(event.getData());
        message.setStatus(Status.CREATED.getCode());
        message.setRetry(0);
        message.setCreateDate(LocalDateTime.now());
        message.setLastModifiedDate(LocalDateTime.now());
        try {
            mqBrokerMessageMapper.insert(message);
        } catch (DuplicateKeyException e) {
            log.info("消费端防重, name: {} key: {} data: {}", event.getName(), event.getKey(), event.getData());
        }
    }

}

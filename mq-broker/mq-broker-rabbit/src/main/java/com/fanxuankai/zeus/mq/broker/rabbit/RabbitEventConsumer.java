package com.fanxuankai.zeus.mq.broker.rabbit;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventConsumer;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MessageReceiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
@Slf4j
public class RabbitEventConsumer implements EventConsumer {

    @Resource
    private MessageReceiveMapper messageReceiveMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public void accept(Event event) {
        MessageReceive messageReceive = new MessageReceive();
        messageReceive.setQueue(event.getName());
        messageReceive.setId(idGenerator.generateId().longValue());
        messageReceive.setCode(event.getKey());
        messageReceive.setContent(event.getData());
        messageReceive.setStatus(Status.CREATED.getCode());
        messageReceive.setRetry(0);
        messageReceive.setCreateDate(LocalDateTime.now());
        messageReceive.setLastModifiedDate(LocalDateTime.now());
        try {
            messageReceiveMapper.insert(messageReceive);
        } catch (DuplicateKeyException e) {
            log.info("消费端防重, name: {} key: {} data: {}", event.getName(), event.getKey(), event.getData());
        }
    }

    @RabbitListener(queues = "user")
    public void proxyMethod(Event event) {
        this.accept(event);
    }

}

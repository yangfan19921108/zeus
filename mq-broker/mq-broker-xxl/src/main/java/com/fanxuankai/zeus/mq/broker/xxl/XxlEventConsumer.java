package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MessageReceiveMapper;
import com.xxl.mq.client.consumer.IMqConsumer;
import com.xxl.mq.client.consumer.MqResult;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author fanxuankai
 */
@Slf4j
@MqConsumer(topic = "user")
public class XxlEventConsumer implements Consumer<String>, IMqConsumer {

    @Resource
    private MessageReceiveMapper messageReceiveMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public void accept(String s) {
        Event event = JSON.parseObject(s, Event.class);
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

    @Override
    public MqResult consume(String s) {
        this.accept(s);
        return MqResult.SUCCESS;
    }

}

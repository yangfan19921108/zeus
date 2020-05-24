package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MsgReceiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractMqConsumer<T> implements MqConsumer<T>, Function<T, Event> {

    @Resource
    private MsgReceiveMapper msgReceiveMapper;

    @Override
    public void accept(T t) {
        Event event = apply(t);
        MsgReceive message = new MsgReceive();
        message.setCode(event.getKey());
        message.setTopic(event.getName());
        message.setData(event.getData());
        message.setStatus(Status.CREATED.getCode());
        message.setRetry(0);
        message.setCreateDate(LocalDateTime.now());
        message.setLastModifiedDate(LocalDateTime.now());
        try {
            msgReceiveMapper.insert(message);
        } catch (DuplicateKeyException ignored) {

        }
    }

    @Override
    public Event apply(T t) {
        return (Event) t;
    }

}

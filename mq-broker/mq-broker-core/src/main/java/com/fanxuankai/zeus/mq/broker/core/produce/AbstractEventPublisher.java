package com.fanxuankai.zeus.mq.broker.core.produce;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
public abstract class AbstractEventPublisher implements EventPublisher {

    @Resource
    protected MsgSendService msgSendService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    protected void persistence(Event event, boolean async) {
        MsgSend msgSend = createMessageSend(event);
        if (async) {
            threadPoolExecutor.execute(() ->
                    msgSendService.save(msgSend));
        } else {
            msgSendService.save(msgSend);
        }
    }

    protected void persistence(List<Event> events, boolean async) {
        List<MsgSend> msgSends = events.stream()
                .map(this::createMessageSend)
                .collect(Collectors.toList());
        if (async) {
            threadPoolExecutor.execute(() ->
                    msgSendService.saveBatch(msgSends));
        } else {
            msgSendService.saveBatch(msgSends);
        }
    }

    protected MsgSend createMessageSend(Event event) {
        MsgSend message = new MsgSend();
        message.setTopic(event.getName());
        message.setCode(event.getKey());
        message.setData(event.getData());
        message.setStatus(Status.CREATED.getCode());
        message.setRetry(0);
        message.setCreateDate(LocalDateTime.now());
        message.setLastModifiedDate(LocalDateTime.now());
        return message;
    }

}

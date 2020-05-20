package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@Slf4j
public class RabbitMessageSendConsumer implements MessageSendConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void accept(MessageSend messageSend) {
        rabbitTemplate.convertAndSend(messageSend.getQueue(), new Event(messageSend.getQueue(), messageSend.getCode()
                , messageSend.getContent()));
        // todo ack 发送确认机制
    }

}

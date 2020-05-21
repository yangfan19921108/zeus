package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.AbstractMessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.core.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@Slf4j
public class RabbitMessageSendConsumer extends AbstractMessageSendConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    protected void onAccept(Event event) {
        rabbitTemplate.convertAndSend(event.getName(), event);
    }

}

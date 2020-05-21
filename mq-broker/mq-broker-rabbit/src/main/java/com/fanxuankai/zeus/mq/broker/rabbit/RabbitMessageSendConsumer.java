package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
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
    public void accept(MqBrokerMessage message) {
        rabbitTemplate.convertAndSend(message.getQueue(), new Event(message.getQueue(), message.getCode()
                , message.getContent()));
    }

}

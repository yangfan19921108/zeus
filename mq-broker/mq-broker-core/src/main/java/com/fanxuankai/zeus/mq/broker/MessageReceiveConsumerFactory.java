package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.EventListenerStrategy;
import com.fanxuankai.zeus.mq.broker.core.MessageReceiveConsumer;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
public class MessageReceiveConsumerFactory implements ApplicationContextAware {

    @Resource
    private MqBrokerProperties mqBrokerProperties;

    private Map<EventListenerStrategy, MessageReceiveConsumer> consumerMap = Collections.emptyMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        consumerMap = applicationContext.getBeansOfType(AbstractMessageReceiveConsumer.class)
                .values()
                .stream()
                .collect(Collectors.toMap(AbstractMessageReceiveConsumer::getEventListenerStrategy, o -> o));
    }

    public MessageReceiveConsumer get(MqBrokerMessage message) {
        return consumerMap.get(Optional.ofNullable(mqBrokerProperties.getEventListenerStrategy().get(message.getQueue()))
                .orElse(EventListenerStrategy.DEFAULT));
    }
}

package com.fanxuankai.zeus.mq.broker.core.consume;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;
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
public class EventDistributorFactory implements ApplicationContextAware {

    @Resource
    private MqBrokerProperties mqBrokerProperties;

    private Map<EventStrategy, AbstractEventDistributor> consumerMap = Collections.emptyMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        consumerMap = applicationContext.getBeansOfType(AbstractEventDistributor.class)
                .values()
                .stream()
                .collect(Collectors.toMap(AbstractEventDistributor::getEventListenerStrategy, o -> o));
    }

    public AbstractEventDistributor get(MsgReceive msg) {
        return consumerMap.get(Optional.ofNullable(mqBrokerProperties.getEventStrategy().get(msg.getTopic()))
                .orElse(EventStrategy.DEFAULT));
    }
}

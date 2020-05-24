package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.core.consume.EventListenerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class MqBrokerRabbitAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata,
                                        @NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        EventListenerFactory.getListeners()
                .forEach(listener ->
                        beanDefinitionRegistry.registerBeanDefinition(listener.event() + "Queue",
                                new RootBeanDefinition(Queue.class, () -> new Queue(listener.event(), true))));
    }
}

package com.fanxuankai.zeus.mq.broker.rabbit;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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
        RabbitListenerBeanRegister.registry(beanDefinitionRegistry);
    }
}

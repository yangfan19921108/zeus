package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.config.EnableMqBrokerAttributes;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
        if (!EnableMqBrokerAttributes.isEnable()) {
            return;
        }
        RabbitListenerBeanRegister.registry(beanDefinitionRegistry);
    }
}

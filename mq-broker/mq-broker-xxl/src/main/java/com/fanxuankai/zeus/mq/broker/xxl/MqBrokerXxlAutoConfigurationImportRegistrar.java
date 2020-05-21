package com.fanxuankai.zeus.mq.broker.xxl;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class MqBrokerXxlAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata,
                                        @NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        XxlConsumerBeanRegister.registry(beanDefinitionRegistry);
    }
}

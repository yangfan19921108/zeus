package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.fanxuankai.zeus.canal.client.mq.core.config.BeanRegistry;
import com.fanxuankai.zeus.canal.client.rabbit.util.JavassistBeanGenerator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class RabbitListenerAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        BeanRegistry.registerWith(registry,
                (mqConsumer, domainType, topic) ->
                        new Class[]{JavassistBeanGenerator.generateRabbitMqConsumer(mqConsumer, domainType, topic)});
    }
}

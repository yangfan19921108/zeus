package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.fanxuankai.zeus.canal.client.mq.core.config.BeanRegistry;
import com.fanxuankai.zeus.canal.client.rabbit.util.JavassistBeanGenerator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author fanxuankai
 */
public class CanalAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanRegistry.registerWith(registry,
                (domainType, topic) -> new Class[]{JavassistBeanGenerator.generateRabbitMqConsumer(domainType, topic)});
    }
}

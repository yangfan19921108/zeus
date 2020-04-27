package com.fanxuankai.zeus.canal.client.xxl.config;

import com.fanxuankai.zeus.canal.client.mq.core.config.BeanRegistry;
import com.fanxuankai.zeus.canal.client.xxl.util.JavassistBeanGenerator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static com.alibaba.otter.canal.protocol.CanalEntry.EventType.*;

/**
 * @author fanxuankai
 */
public class XxlConsumerAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanRegistry.registerWith(registry,
                (mqConsumer, domainType, topic) -> new Class[]{
                        JavassistBeanGenerator.generateXxlMqConsumer(mqConsumer, domainType, topic, INSERT),
                        JavassistBeanGenerator.generateXxlMqConsumer(mqConsumer, domainType, topic, UPDATE),
                        JavassistBeanGenerator.generateXxlMqConsumer(mqConsumer, domainType, topic, DELETE)
                }
        );
    }
}

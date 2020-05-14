package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import com.fanxuankai.zeus.canal.client.mq.core.config.BeanRegistry;
import com.fanxuankai.zeus.canal.client.mq.core.config.CanalToMqScanner;
import com.fanxuankai.zeus.canal.client.rabbit.util.JavassistBeanGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class RabbitAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        BeanRegistry.registerWith(registry,
                (mqConsumer, domainType, topic) ->
                        new Class[]{JavassistBeanGenerator.generateRabbitMqConsumer(mqConsumer, domainType, topic)});
        if (EnableCanalAttributes.isEnabled()
                && !StringUtils.isBlank(CanalToMqScanner.CONSUME_CONFIGURATION.getFilter())) {
            registry.registerBeanDefinition(CanalWorkerAutoConfiguration.class.getName(),
                    new RootBeanDefinition(CanalWorkerAutoConfiguration.class));
        }
    }

}

package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.mq.core.util.JavassistBeanGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class MqAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        BeanRegistry.registerWith(registry, JavassistBeanGenerator::generateRabbitMqConsumer);
        if (!StringUtils.isBlank(CanalToMqScanner.CONSUME_CONFIGURATION.getFilter())) {
            registry.registerBeanDefinition(CanalWorkerAutoConfiguration.class.getName(),
                    new RootBeanDefinition(CanalWorkerAutoConfiguration.class));
        }
    }

}

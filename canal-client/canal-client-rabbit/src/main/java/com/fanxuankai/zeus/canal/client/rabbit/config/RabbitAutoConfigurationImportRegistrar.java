package com.fanxuankai.zeus.canal.client.rabbit.config;

import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Arrays;

/**
 * @author fanxuankai
 */
public class RabbitAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        if (!EnableCanalAttributes.isEnabled()) {
            return;
        }
        Arrays.asList(CanalWorkerAutoConfiguration.class, RabbitListenerAutoConfiguration.class)
                .forEach(aClass ->
                        registry.registerBeanDefinition(aClass.getName(), new RootBeanDefinition(aClass)));
    }

}

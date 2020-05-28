package com.fanxuankai.zeus.canal.client.es.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class EsAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        if (!StringUtils.isBlank(CanalToEsScanner.CONSUME_CONFIGURATION.getFilter())) {
            registry.registerBeanDefinition(CanalWorkerAutoConfiguration.class.getName(),
                    new RootBeanDefinition(CanalWorkerAutoConfiguration.class));
        }
    }

}

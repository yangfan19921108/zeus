package com.fanxuankai.zeus.canal.client.redis.config;

import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class RedisAutoConfigurationImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        if (!EnableCanalAttributes.isEnabled()) {
            return;
        }
        registry.registerBeanDefinition(CanalWorkerAutoConfiguration.class.getName(),
                new RootBeanDefinition(CanalWorkerAutoConfiguration.class));
        BeanRegistry.registerWith(registry);
    }

}

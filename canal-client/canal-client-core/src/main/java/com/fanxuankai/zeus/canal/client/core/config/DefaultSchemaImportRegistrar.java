package com.fanxuankai.zeus.canal.client.core.config;

import com.fanxuankai.zeus.canal.client.core.metadata.DefaultSchemaAttributes;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class DefaultSchemaImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        DefaultSchemaAttributes.from(importingClassMetadata);
    }

}

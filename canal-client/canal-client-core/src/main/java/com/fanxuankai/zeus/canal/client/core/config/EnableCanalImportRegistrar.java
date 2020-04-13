package com.fanxuankai.zeus.canal.client.core.config;

import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author fanxuankai
 */
public class EnableCanalImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        EnableCanalAttributes.from(importingClassMetadata);
    }
}

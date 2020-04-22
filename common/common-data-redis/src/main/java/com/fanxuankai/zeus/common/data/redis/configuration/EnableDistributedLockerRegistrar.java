package com.fanxuankai.zeus.common.data.redis.configuration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author fanxuankai
 */
public class EnableDistributedLockerRegistrar implements ImportBeanDefinitionRegistrar {

    static String PACKAGE_NAME = "";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        PACKAGE_NAME = ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }

}

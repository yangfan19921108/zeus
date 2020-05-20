package com.fanxuankai.zeus.mq.broker.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class EnableMqBrokerImportRegistrar implements ImportSelector {

    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        EnableMqBrokerAttributes.from(annotationMetadata);
        return new String[0];
    }
}

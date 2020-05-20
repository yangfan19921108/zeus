package com.fanxuankai.zeus.mq.broker.config;

import lombok.Getter;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author fanxuankai
 */
public class EnableMqBrokerAttributes {
    @Getter
    private static String basePackage;
    @Getter
    private static boolean enable;

    public static void from(AnnotationMetadata metadata) {
        basePackage = ClassUtils.getPackageName(metadata.getClassName());
        enable = true;
    }
}

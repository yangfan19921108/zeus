package com.fanxuankai.zeus.canal.client.core.metadata;

import com.fanxuankai.zeus.canal.client.core.annotation.DefaultSchema;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * DefaultSchema 注解属性工具类
 *
 * @author fanxuankai
 */
public class DefaultSchemaAttributes {

    private static final String BASE_PACKAGE = "basePackage";
    private static AnnotationAttributes attributes = new AnnotationAttributes();

    public static void from(AnnotationMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(DefaultSchema.class.getName(),
                false);
        attributes = AnnotationAttributes.fromMap(annotationAttributes);
        if (attributes == null) {
            throw new IllegalArgumentException(String.format(
                    "@%s is not present on importing class '%s' as expected",
                    DefaultSchema.class.getSimpleName(), metadata.getClassName()));
        }
        attributes.put(BASE_PACKAGE, ClassUtils.getPackageName(metadata.getClassName()));
    }

    public static String getSchema() {
        return attributes.getString("value");
    }

    public static String getBasePackage() {
        return attributes.getString(BASE_PACKAGE);
    }

}

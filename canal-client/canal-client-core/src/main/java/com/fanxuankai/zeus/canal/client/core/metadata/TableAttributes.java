package com.fanxuankai.zeus.canal.client.core.metadata;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fanxuankai
 */
public class TableAttributes {

    private static final Map<Class<?>, TableAttributes> CACHE = new ConcurrentHashMap<>();

    private final AnnotationAttributes annotationAttributes;

    public TableAttributes(AnnotationAttributes annotationAttributes) {
        this.annotationAttributes = annotationAttributes;
    }

    public static Optional<TableAttributes> from(Class<?> type) {
        TableAttributes tableAttributes = CACHE.get(type);
        if (tableAttributes == null) {
            AnnotationAttributes annotationAttributes = fromType(type);
            if (annotationAttributes == null) {
                return Optional.empty();
            }
            tableAttributes = new TableAttributes(annotationAttributes);
            CACHE.put(type, tableAttributes);
        }
        return Optional.of(tableAttributes);
    }

    @SuppressWarnings("unchecked")
    private static AnnotationAttributes fromType(Class<?> type) {
        try {
            Class<Annotation> tableClass = (Class<Annotation>) Class.forName("javax.persistence.Table");
            Annotation annotation = type.getAnnotation(tableClass);
            if (annotation != null) {
                return AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation));
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public String getName() {
        return annotationAttributes.getString("name");
    }

    public String getSchema() {
        return annotationAttributes.getString("schema");
    }

}

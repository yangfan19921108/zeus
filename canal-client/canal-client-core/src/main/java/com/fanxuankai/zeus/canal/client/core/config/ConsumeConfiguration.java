package com.fanxuankai.zeus.canal.client.core.config;

import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.DefaultSchemaAttributes;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消费配置注解扫描器
 * <p>
 * A: 注解
 * M: 注解的元数据
 *
 * @author fanxuankai
 */
@Slf4j
public class ConsumeConfiguration<A extends Annotation, M> {
    /**
     * 所有实体类
     */
    @Getter
    private final Set<Class<?>> domainClasses = Sets.newHashSet();
    /**
     * 所有 @CanalTable 元数据
     */
    private final Set<CanalTableMetadata> canalTableMetadataSet = Sets.newHashSet();
    /**
     * key: 实体类 value: @CanalTable 元数据
     */
    private final Map<Class<?>, CanalTableMetadata> canalTableMetadataByDomain = Maps.newHashMap();
    /**
     * key: 数据库表名(schema.table) value: 注解元数据
     */
    private final Map<String, M> annotationMetadataByTable = Maps.newHashMap();
    /**
     * key: 数据库表名(schema.table) value: 注解
     */
    private final Map<String, A> annotationByTable = Maps.newHashMap();
    /**
     * key: 数据库表名(schema.table) value: 实体类
     */
    private final Map<String, Class<?>> domainByTable = Maps.newHashMap();

    /**
     * @param annotationClass  定义的注解
     * @param metadataFunction 注解转换
     */
    public ConsumeConfiguration(@NonNull Class<A> annotationClass, @Nullable Function<A, M> metadataFunction) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages(DefaultSchemaAttributes.getBasePackage())
                        .setScanners(new TypeAnnotationsScanner(), new FieldAnnotationsScanner(),
                                new MethodAnnotationsScanner())
                );
        Stopwatch sw = Stopwatch.createStarted();
        Set<Class<?>> domainClasses = r.getTypesAnnotatedWith(annotationClass, true);
        sw.stop();
        log.info("Finished {} scanning in {}ms, Found {}.", annotationClass.getSimpleName(),
                sw.elapsed(TimeUnit.MILLISECONDS), domainClasses.size());
        this.domainClasses.addAll(domainClasses);
        for (Class<?> domainClass : domainClasses) {
            A annotation = domainClass.getAnnotation(annotationClass);
            CanalTableMetadata canalTableMetadata = new CanalTableMetadata(domainClass);
            canalTableMetadataSet.add(canalTableMetadata);
            canalTableMetadataByDomain.put(domainClass, canalTableMetadata);
            String fullTableName = CommonUtils.fullTableName(canalTableMetadata.getSchema(),
                    canalTableMetadata.getName());
            domainByTable.put(fullTableName, domainClass);
            annotationByTable.put(fullTableName, annotation);
            if (metadataFunction != null) {
                M metadata = metadataFunction.apply(annotation);
                annotationMetadataByTable.put(fullTableName, metadata);
            }
        }
    }

    public String getFilter() {
        return canalTableMetadataSet.stream()
                .map(canalTableMetadata ->
                        CommonUtils.fullTableName(canalTableMetadata.getSchema(), canalTableMetadata.getName()))
                .collect(Collectors.joining(","));
    }

    public Class<?> getDomain(EntryWrapper entryWrapper) {
        return domainByTable.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    @Nullable
    public A getAnnotation(EntryWrapper entryWrapper) {
        return annotationByTable.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    @Nullable
    public M getMetadata(EntryWrapper entryWrapper) {
        return getMetadata(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    @Nullable
    public M getMetadata(String schema, String table) {
        return getMetadata(CommonUtils.fullTableName(schema, table));
    }

    private M getMetadata(String tableName) {
        return annotationMetadataByTable.get(tableName);
    }

    public CanalTableMetadata getCanalTableMetadata(Class<?> domainType, boolean createDefault) {
        CanalTableMetadata canalTableMetadata = canalTableMetadataByDomain.get(domainType);
        if (canalTableMetadata == null && createDefault) {
            synchronized (this) {
                canalTableMetadata = new CanalTableMetadata(domainType);
                canalTableMetadataByDomain.put(domainType, canalTableMetadata);
            }
        }
        return canalTableMetadata;
    }

}

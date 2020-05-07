package com.fanxuankai.zeus.canal.client.core.util;

import com.fanxuankai.zeus.canal.client.core.annotation.CanalTable;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 接口实现类扫描器
 * <p>
 * I: 接口
 * A: 注解
 * M: 注解的元数据
 *
 * @author fanxuankai
 */
@Slf4j
public class InterfaceBeanScanner<I, A extends Annotation, M> {
    /**
     * 所有接口实现类
     */
    public final Set<Class<? extends I>> ALL_INTERFACE_BEAN_CLASSES = Sets.newHashSet();
    /**
     * 所有 @CanalTable 元数据
     */
    private final Set<CanalTableMetadata> ALL_CANAL_TABLE_METADATA = Sets.newHashSet();

    /**
     * key: 接口实现类 value: 实体类
     */
    private final Map<Class<? extends I>, Class<?>> DOMAIN_CLASS_BY_INTERFACE_BEAN = Maps.newHashMap();
    /**
     * key: 实体类 value: @CanalTable 元数据
     */
    private final Map<Class<?>, CanalTableMetadata> CANAL_TABLE_METADATA_BY_DOMAIN = Maps.newHashMap();
    /**
     * key: 数据库表名(schema.table) value: 注解元数据
     */
    private final Map<String, M> ANNOTATION_METADATA_BY_TABLE = Maps.newHashMap();
    /**
     * key: 数据库表名(schema.table) value: 接口实现类
     */
    private final Map<String, Class<? extends I>> INTERFACE_BEAN_BY_TABLE = Maps.newHashMap();

    /**
     * @param iClass           定义的接口
     * @param aClass           定义的注解
     * @param iPredicate       子类断言
     * @param pPredicate       父类断言
     * @param domainClassIndex 第几个泛型
     * @param mFunction        注解转换
     */
    public InterfaceBeanScanner(Class<I> iClass, Class<A> aClass,
                                Predicate<Class<? extends I>> iPredicate,
                                Predicate<ParameterizedType> pPredicate,
                                int domainClassIndex,
                                Function<A, M> mFunction) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages(EnableCanalAttributes.getBasePackage())
                        .setScanners(new SubTypesScanner())
                );
        Stopwatch sw = Stopwatch.createStarted();
        Set<Class<? extends I>> interfaceBeanClasses = r.getSubTypesOf(iClass);
        sw.stop();
        log.info("Finished {} scanning in {}ms. Found {} {} interfaces.", iClass.getSimpleName(),
                sw.elapsed(TimeUnit.MILLISECONDS), interfaceBeanClasses.size(), iClass.getSimpleName());
        List<Bean> beans = Lists.newArrayList();
        for (Class<? extends I> interfaceBeanClass : interfaceBeanClasses) {
            if (!iPredicate.test(interfaceBeanClass)) {
                continue;
            }
            Type[] genericInterfaces = interfaceBeanClass.getGenericInterfaces();
            Class<?> domainType = null;
            for (Type genericInterface : genericInterfaces) {
                ParameterizedType p = (ParameterizedType) genericInterface;
                if (pPredicate.test(p)) {
                    domainType = (Class<?>) p.getActualTypeArguments()[domainClassIndex];
                    break;
                }
            }
            if (domainType == null) {
                continue;
            }
            A annotation = interfaceBeanClass.getAnnotation(aClass);
            M metadata = mFunction.apply(annotation);
            CanalTableMetadata canalTableMetadata = new CanalTableMetadata(domainType.getAnnotation(CanalTable.class)
                    , domainType);
            beans.add(new Bean(interfaceBeanClass, domainType, metadata, canalTableMetadata));
            CanalTableCache.put(canalTableMetadata);
        }
        setup(beans);
    }

    public Class<?> getDomainType(Class<? extends I> redisRepositoryClass) {
        return DOMAIN_CLASS_BY_INTERFACE_BEAN.get(redisRepositoryClass);
    }

    public String getFilter() {
        return ALL_CANAL_TABLE_METADATA.stream()
                .map(canalTableMetadata ->
                        CommonUtils.fullTableName(canalTableMetadata.getSchema(), canalTableMetadata.getName()))
                .collect(Collectors.joining(","));
    }

    public Class<? extends I> getInterfaceBeanClass(EntryWrapper entryWrapper) {
        return INTERFACE_BEAN_BY_TABLE.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    public M getMetadata(EntryWrapper entryWrapper) {
        return ANNOTATION_METADATA_BY_TABLE.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    public M getMetadata(String schema, String table) {
        return ANNOTATION_METADATA_BY_TABLE.get(CommonUtils.fullTableName(schema, table));
    }

    public CanalTableMetadata getCanalTableMetadata(Class<?> domainType) {
        return CANAL_TABLE_METADATA_BY_DOMAIN.get(domainType);
    }

    private void setup(List<Bean> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        for (Bean bean : beans) {
            Class<? extends I> redisRepositoryClass = bean.redisRepositoryClass;
            CanalTableMetadata canalTableMeta = bean.canalTableMeta;
            DOMAIN_CLASS_BY_INTERFACE_BEAN.put(redisRepositoryClass, bean.domainType);
            ALL_CANAL_TABLE_METADATA.add(canalTableMeta);
            String fullTableName = CommonUtils.fullTableName(canalTableMeta.getSchema(), canalTableMeta.getName());
            INTERFACE_BEAN_BY_TABLE.put(fullTableName, redisRepositoryClass);
            ANNOTATION_METADATA_BY_TABLE.put(fullTableName, bean.canalToRedisMetadata);
            CANAL_TABLE_METADATA_BY_DOMAIN.put(bean.domainType, canalTableMeta);
            ALL_INTERFACE_BEAN_CLASSES.add(redisRepositoryClass);
        }
    }

    @AllArgsConstructor
    @Getter
    private class Bean {
        private final Class<? extends I> redisRepositoryClass;
        private final Class<?> domainType;
        private final M canalToRedisMetadata;
        private final CanalTableMetadata canalTableMeta;
    }
}

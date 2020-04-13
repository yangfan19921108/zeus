package com.fanxuankai.zeus.canal.client.core.util;

import com.fanxuankai.zeus.canal.client.core.annotation.CanalTable;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bean 扫描器
 *
 * @author fanxuankai
 */
@Slf4j
public class InterfaceBeanScanner<I, A extends Annotation, M> {
    public final List<Class<? extends I>> ALL_INTERFACE_BEAN_CLASSES = Lists.newArrayList();
    private final Map<Class<? extends I>, Class<?>> DOMAIN_TYPE_CACHE = Maps.newHashMap();
    private final List<CanalTableMetadata> ALL_CANAL_TABLE_METADATA = Lists.newArrayList();
    private final Map<Class<?>, CanalTableMetadata> CANAL_TABLE_METADATA_CACHE = Maps.newHashMap();
    private final Map<String, M> METADATA_CACHE = Maps.newHashMap();
    private final Map<String, Class<? extends I>> CLASS_BY_FULL_TABLE_NAME = Maps.newHashMap();

    /**
     * @param iClass     定义的接口
     * @param aClass     定义的注解
     * @param iPredicate 子类断言
     * @param pPredicate 父类断言
     * @param mFunction  注解转换
     */
    public InterfaceBeanScanner(Class<I> iClass, Class<A> aClass,
                                Predicate<Class<? extends I>> iPredicate,
                                Predicate<ParameterizedType> pPredicate,
                                Function<A, M> mFunction) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages(EnableCanalAttributes.getBasePackage())
                        .setScanners(new SubTypesScanner())
                );
        long l = System.currentTimeMillis();
        Set<Class<? extends I>> interfaceBeanClasses = r.getSubTypesOf(iClass);
        log.info("Finished {} scanning in {}ms. Found {} {} interfaces.", iClass.getName(),
                System.currentTimeMillis() - l, interfaceBeanClasses.size(), iClass.getName());
        List<Bean> beans = Lists.newArrayList();
        for (Class<? extends I> interfaceBeanClass : interfaceBeanClasses) {
            if (!iPredicate.test(interfaceBeanClass)) {
                continue;
            }
            Type[] genericInterfaces = interfaceBeanClass.getGenericInterfaces();
            Class<?> domainType = null;
            for (Type genericInterface : genericInterfaces) {
                ParameterizedType p = (ParameterizedType) genericInterface;
                if (!pPredicate.test(p)) {
                    continue;
                }
                domainType = (Class<?>) p.getActualTypeArguments()[0];
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
        return DOMAIN_TYPE_CACHE.get(redisRepositoryClass);
    }

    public String getFilter() {
        return ALL_CANAL_TABLE_METADATA.stream()
                .map(canalTableMetadata ->
                        CommonUtils.fullTableName(canalTableMetadata.getSchema(), canalTableMetadata.getName()))
                .collect(Collectors.joining(","));
    }

    public Class<? extends I> getInterfaceBeanClass(EntryWrapper entryWrapper) {
        return CLASS_BY_FULL_TABLE_NAME.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    public M getMetadata(EntryWrapper entryWrapper) {
        return METADATA_CACHE.get(CommonUtils.fullTableName(entryWrapper.getSchemaName(),
                entryWrapper.getTableName()));
    }

    public CanalTableMetadata getCanalTableMetadata(Class<?> domainType) {
        return CANAL_TABLE_METADATA_CACHE.get(domainType);
    }

    private void setup(List<Bean> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        for (Bean bean : beans) {
            Class<? extends I> redisRepositoryClass = bean.redisRepositoryClass;
            CanalTableMetadata canalTableMeta = bean.canalTableMeta;
            DOMAIN_TYPE_CACHE.put(redisRepositoryClass, bean.domainType);
            ALL_CANAL_TABLE_METADATA.add(canalTableMeta);
            String fullTableName = CommonUtils.fullTableName(canalTableMeta.getSchema(), canalTableMeta.getName());
            CLASS_BY_FULL_TABLE_NAME.put(fullTableName, redisRepositoryClass);
            METADATA_CACHE.put(fullTableName, bean.canalToRedisMetadata);
            CANAL_TABLE_METADATA_CACHE.put(bean.domainType, canalTableMeta);
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

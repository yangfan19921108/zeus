package com.fanxuankai.zeus.canal.client.core.util;

import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.metadata.EnableCanalAttributes;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 接口实现类扫描器
 * <p>
 * I: 接口
 *
 * @author fanxuankai
 */
@Slf4j
public class InterfaceBeanScanner<I> {
    /**
     * 所有接口实现类
     */
    public final Set<Class<? extends I>> interfaceBeanSet = Sets.newHashSet();

    /**
     * key: 接口实现类 value: 实体类
     */
    private final Map<Class<? extends I>, Class<?>> domainByInterfaceBean = Maps.newHashMap();

    /**
     * @param interfaceClass             定义的接口
     * @param interfacePredicate         子类断言
     * @param parameterizedTypePredicate 父类断言
     * @param domainClassIndex           第几个泛型
     */
    public InterfaceBeanScanner(Class<I> interfaceClass,
                                Predicate<Class<? extends I>> interfacePredicate,
                                Predicate<ParameterizedType> parameterizedTypePredicate,
                                int domainClassIndex) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages(EnableCanalAttributes.getBasePackage())
                        .setScanners(new SubTypesScanner())
                );
        Stopwatch sw = Stopwatch.createStarted();
        Set<Class<? extends I>> interfaceBeanClasses = Collections.emptySet();
        try {
            interfaceBeanClasses = r.getSubTypesOf(interfaceClass);
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
        }
        sw.stop();
        log.info("Finished {} scanning in {}ms. Found {} {} interfaces.", interfaceClass.getSimpleName(),
                sw.elapsed(TimeUnit.MILLISECONDS), interfaceBeanClasses.size(), interfaceClass.getSimpleName());
        for (Class<? extends I> interfaceBeanClass : interfaceBeanClasses) {
            if (!interfacePredicate.test(interfaceBeanClass)) {
                continue;
            }
            Type[] genericInterfaces = interfaceBeanClass.getGenericInterfaces();
            Class<?> domainType = null;
            for (Type genericInterface : genericInterfaces) {
                ParameterizedType p = (ParameterizedType) genericInterface;
                if (parameterizedTypePredicate.test(p)) {
                    domainType = (Class<?>) p.getActualTypeArguments()[domainClassIndex];
                    break;
                }
            }
            if (domainType == null) {
                continue;
            }
            CanalTableMetadata canalTableMetadata = new CanalTableMetadata(domainType);
            CanalTableCache.put(canalTableMetadata);
            domainByInterfaceBean.put(interfaceBeanClass, domainType);
            interfaceBeanSet.add(interfaceBeanClass);
        }
    }

    public Class<?> getDomainType(Class<? extends I> interfaceBeanClass) {
        return domainByInterfaceBean.get(interfaceBeanClass);
    }
}

package com.fanxuankai.zeus.canal.client.redis.configuration;

import com.fanxuankai.zeus.canal.client.redis.util.JavassistBeanGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static com.fanxuankai.zeus.canal.client.redis.configuration.RedisRepositoryScanner.INTERFACE_BEAN_SCANNER;

/**
 * @author fanxuankai
 */
@Slf4j
public class BeanRegistry {

    /**
     * 注册 bean
     *
     * @param registry BeanDefinitionRegistry
     */
    public static void registerWith(BeanDefinitionRegistry registry) {
        INTERFACE_BEAN_SCANNER.ALL_INTERFACE_BEAN_CLASSES
                .parallelStream()
                .map(redisRepositoryClass -> {
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(
                            JavassistBeanGenerator.generateRedisRepository(redisRepositoryClass,
                                    INTERFACE_BEAN_SCANNER.getDomainType(redisRepositoryClass)));
                    return new BeanDefinitionHolder(beanDefinition, redisRepositoryClass.getName());
                })
                .forEach(bh -> BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry));
    }

}

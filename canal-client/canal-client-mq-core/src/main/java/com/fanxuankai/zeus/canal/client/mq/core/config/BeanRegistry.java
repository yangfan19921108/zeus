package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.util.QueueNameUtils;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static com.fanxuankai.zeus.canal.client.mq.core.config.MqConsumerScanner.INTERFACE_BEAN_SCANNER;

/**
 * @author fanxuankai
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class BeanRegistry {

    /**
     * 自动生成 MQ 消费者且注册为 Spring bean
     *
     * @param beanGenerator Bean 生成器
     * @param registry      BeanDefinitionRegistry
     */
    public static void registerWith(BeanDefinitionRegistry registry, BeanGenerator beanGenerator) {
        INTERFACE_BEAN_SCANNER.ALL_INTERFACE_BEAN_CLASSES
                .parallelStream()
                .forEach(mqConsumerClass -> {
                    Class<?> domainType = INTERFACE_BEAN_SCANNER.getDomainType(mqConsumerClass);
                    CanalTableMetadata tableMetadata = INTERFACE_BEAN_SCANNER.getCanalTableMetadata(domainType);
                    CanalToMqMetadata mqMetadata = INTERFACE_BEAN_SCANNER.getMetadata(tableMetadata.getSchema(),
                            tableMetadata.getName());
                    String topic;
                    if (StringUtils.isNotBlank(mqMetadata.getName())) {
                        topic = QueueNameUtils.customName(mqMetadata.getName());
                    } else {
                        topic = QueueNameUtils.name(tableMetadata.getSchema(), tableMetadata.getName());
                    }
                    for (Class<?> mqConsumerBeanClass : beanGenerator.generate(mqConsumerClass, domainType, topic)) {
                        register(mqConsumerBeanClass, registry);
                    }
                });
    }

    private static void register(Class<?> mqConsumerBeanClass, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(mqConsumerBeanClass);
        BeanDefinitionHolder bh = new BeanDefinitionHolder(beanDefinition, mqConsumerBeanClass.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry);
    }

    public interface BeanGenerator {
        /**
         * 生成类
         *
         * @param mqConsumer MqConsumer 子类
         * @param domainType MqConsumer 的泛型
         * @param topic      MQ topic
         * @return 1个或者多个运行时生成的类
         */
        Class<?>[] generate(Class<? extends MqConsumer> mqConsumer, Class<?> domainType, String topic);
    }
}

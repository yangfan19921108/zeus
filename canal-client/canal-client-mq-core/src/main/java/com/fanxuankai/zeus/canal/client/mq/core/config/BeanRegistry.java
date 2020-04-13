package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.util.QueueNameUtils;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
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
     * @param registry BeanDefinitionRegistry
     */
    public static void registerWith(BeanDefinitionRegistry registry, BeanGenerator beanGenerator) {
        for (Class<? extends MqConsumer> mqConsumerClass : INTERFACE_BEAN_SCANNER.ALL_INTERFACE_BEAN_CLASSES) {
            Class<?> domainType = INTERFACE_BEAN_SCANNER.getDomainType(mqConsumerClass);
            CanalTableMetadata tableMetadata = INTERFACE_BEAN_SCANNER.getCanalTableMetadata(domainType);
            MqConsumer<?> mqConsumer;
            try {
                mqConsumer = mqConsumerClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("MqConsumer 实例化异常", e);
            }
            String topic = QueueNameUtils.name(tableMetadata.getSchema(), tableMetadata.getName());
            for (Class<?> mqConsumerBeanClass : beanGenerator.generate(domainType, topic)) {
                register(mqConsumerBeanClass, mqConsumer, registry);
            }
        }
    }

    private static void register(Class<?> mqConsumerBeanClass, MqConsumer<?> mqConsumer,
                                 BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(mqConsumerBeanClass);
        ConstructorArgumentValues cav = beanDefinition.getConstructorArgumentValues();
        cav.addGenericArgumentValue(mqConsumer);
        BeanDefinitionHolder bh = new BeanDefinitionHolder(beanDefinition, mqConsumerBeanClass.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry);
    }

    public interface BeanGenerator {
        /**
         * 生成类
         *
         * @param domainType MqConsumer 的泛型类
         * @param topic      MQ topic
         * @return 1个或者多个运行时生成的类
         */
        Class<?>[] generate(Class<?> domainType, String topic);
    }
}

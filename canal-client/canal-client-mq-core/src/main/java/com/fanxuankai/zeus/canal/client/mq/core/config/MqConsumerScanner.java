package com.fanxuankai.zeus.canal.client.mq.core.config;

import com.fanxuankai.zeus.canal.client.core.util.InterfaceBeanScanner;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.metadata.CanalToMqMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author fanxuankai
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class MqConsumerScanner {

    public static final InterfaceBeanScanner<MqConsumer, CanalToMq, CanalToMqMetadata> INTERFACE_BEAN_SCANNER;

    static {
        Predicate<ParameterizedType> pPredicate = p -> Objects.equals(p.getRawType(), MqConsumer.class);
        INTERFACE_BEAN_SCANNER = new InterfaceBeanScanner<>(MqConsumer.class, CanalToMq.class, aClass -> true,
                pPredicate, 0, CanalToMqMetadata::new);
    }

}

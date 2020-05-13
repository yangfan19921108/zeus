package com.fanxuankai.zeus.canal.client.redis.config;

import com.fanxuankai.zeus.canal.client.core.util.InterfaceBeanScanner;
import com.fanxuankai.zeus.canal.client.redis.repository.RedisRepository;
import com.fanxuankai.zeus.canal.client.redis.repository.SimpleRedisRepository;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author fanxuankai
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class RedisRepositoryScanner {

    public static final InterfaceBeanScanner<RedisRepository> INTERFACE_BEAN_SCANNER;

    static {
        Predicate<Class<? extends RedisRepository>> iPredicate = aClass -> !Objects.equals(aClass,
                SimpleRedisRepository.class);
        Predicate<ParameterizedType> pPredicate = p -> Objects.equals(p.getRawType(), RedisRepository.class);
        INTERFACE_BEAN_SCANNER = new InterfaceBeanScanner<>(RedisRepository.class, iPredicate, pPredicate, 0);
    }

}

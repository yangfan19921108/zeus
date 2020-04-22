package com.fanxuankai.zeus.common.data.redis.annotation;

import com.fanxuankai.zeus.common.data.redis.configuration.EnableDistributedLockerConfiguration;
import com.fanxuankai.zeus.common.data.redis.configuration.EnableDistributedLockerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EnableDistributedLockerRegistrar.class, EnableDistributedLockerConfiguration.class})
public @interface EnableDistributedLocker {
}

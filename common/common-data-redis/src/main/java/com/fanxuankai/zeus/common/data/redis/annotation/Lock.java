package com.fanxuankai.zeus.common.data.redis.annotation;

import com.fanxuankai.zeus.common.data.redis.lock.DistributedLocker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁的资源
     *
     * @return 默认为前缀+类名+方法名
     */
    String resource() default "";

    /**
     * 等待锁的时间
     *
     * @return ms
     */
    long waitTimeMillis() default DistributedLocker.WAIT_TIME_MILLI;

    /**
     * 占用锁的时间
     *
     * @return ms
     */
    long leaseTimeMillis() default DistributedLocker.LEASE_TIME_MILLIS;

}

package com.fanxuankai.zeus.common.data.redis.aop;

import com.fanxuankai.zeus.common.data.redis.annotation.Lock;
import com.fanxuankai.zeus.common.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.common.data.redis.lock.DistributedLocker;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author fanxuankai
 */
public class LockMethodInterceptor implements MethodInterceptor {

    private final DistributedLocker distributedLocker;

    public LockMethodInterceptor(DistributedLocker distributedLocker) {
        this.distributedLocker = distributedLocker;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Lock lock = methodInvocation.getMethod().getAnnotation(Lock.class);
        if (lock == null) {
            return methodInvocation.proceed();
        }
        String lockResource = getLockResource(lock, methodInvocation);
        return distributedLocker.lock(lockResource, lock.waitTimeMillis(),
                lock.leaseTimeMillis(), () -> {
                    try {
                        return methodInvocation.proceed();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                });
    }

    private String getLockResource(Lock lock, MethodInvocation methodInvocation) {
        String resource = lock.resource();
        if (StringUtils.isEmpty(resource)) {
            Method method = methodInvocation.getMethod();
            resource = method.getDeclaringClass().getName() + "." + method.getName();
        }
        return RedisKeyPrefix.LOCK.getValue() + "." + resource;
    }
}

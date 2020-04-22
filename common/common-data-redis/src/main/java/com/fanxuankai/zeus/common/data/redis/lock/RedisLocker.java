package com.fanxuankai.zeus.common.data.redis.lock;

import com.fanxuankai.zeus.common.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.common.data.redis.execption.LockException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class RedisLocker implements DistributedLocker {

    private final static String LOCKER_PREFIX = RedisKeyPrefix.LOCK.getValue();

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> T lock(String resource, long waitTime, long releaseTime, Callable<T> callable) throws LockException {
        RLock lock = null;
        try {
            String key = LOCKER_PREFIX + "." + resource;
            lock = redissonClient.getLock(key);
            if (lock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS)) {
                return callable.call();
            }
            throw new LockException("加锁失败");
        } catch (InterruptedException e) {
            throw new LockException("等待锁被中断", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T lock(String resource, Callable<T> callable) throws LockException {
        return lock(resource, DistributedLocker.WAIT_TIME_MILLI, DistributedLocker.LEASE_TIME_MILLIS, callable);
    }
}
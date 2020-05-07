package com.fanxuankai.zeus.data.redis.lock;

import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.data.redis.execption.LockFailureException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    @Override
    public <T> T lock(String key, long waitTime, long releaseTime, Callable<T> callable) throws LockFailureException {
        RLock lock = null;
        try {
            key = LOCKER_PREFIX + "." + key;
            lock = redissonClient.getLock(key);
            if (lock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS)) {
                return callable.call();
            }
        } catch (InterruptedException e) {
            throw new LockFailureException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        throw new LockFailureException();
    }

    @Override
    public <T> T lock(String key, Callable<T> callable) throws LockFailureException {
        return lock(key, WAIT_TIME_MILLI, LEASE_TIME_MILLIS, callable);
    }
}
package com.fanxuankai.zeus.data.redis.lock;

import com.fanxuankai.zeus.data.redis.execption.LockFailureException;

import java.util.concurrent.Callable;

/**
 * 获取锁管理类
 *
 * @author fanxuankai
 */
public interface DistributedLocker {

    long WAIT_TIME_MILLI = 1000;
    long LEASE_TIME_MILLIS = 3000;

    /**
     * 加分布式锁
     * 加锁成功执行回调函数, 执行完成或者 releaseTime 超时自动释放锁资源
     * 执行失败会多次重试直至 waitTime 超时
     *
     * @param <T>         do in lock item type
     * @param key         key
     * @param waitTime    等待锁的时间
     * @param releaseTime 自动释放锁的时间
     * @param callable    获取锁后的回调
     * @return doInLock 返回的数据
     * @throws LockFailureException 加锁异常, 未获取到锁或者等待锁被中断
     */
    <T> T lock(String key, long waitTime, long releaseTime, Callable<T> callable) throws LockFailureException;

    /**
     * 加分布式锁
     * 加锁成功执行回调函数, 执行完成或者 releaseTime 超时自动释放锁资源
     * 执行失败会多次重试直至 waitTime 超时
     *
     * @param <T>      do in lock item type
     * @param key      key
     * @param callable 获取锁后的回调
     * @return doInLock 返回的数据
     * @throws LockFailureException 加锁异常, 未获取到锁或者等待锁被中断
     */
    <T> T lock(String key, Callable<T> callable) throws LockFailureException;

}
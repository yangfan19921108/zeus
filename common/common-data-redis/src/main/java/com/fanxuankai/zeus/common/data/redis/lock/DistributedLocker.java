package com.fanxuankai.zeus.common.data.redis.lock;

import com.fanxuankai.zeus.common.data.redis.execption.LockException;

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
     * @param resource    锁资源
     * @param waitTime    等待锁的时间
     * @param releaseTime 自动释放锁的时间
     * @param callable    获取锁后的回调
     * @param <T>         do in lock item type
     * @return doInLock 返回的数据
     * @throws LockException 加锁异常, 未获取到锁或者等待锁被中断
     */
    <T> T lock(String resource, long waitTime, long releaseTime, Callable<T> callable) throws LockException;

    /**
     * 加分布式锁
     * 加锁成功执行回调函数, 执行完成或者 releaseTime 超时自动释放锁资源
     * 执行失败会多次重试直至 waitTime 超时
     *
     * @param resource 锁资源
     * @param callable 获取锁后的回调
     * @param <T>      do in lock item type
     * @return doInLock 返回的数据
     * @throws LockException 加锁异常, 未获取到锁或者等待锁被中断
     */
    <T> T lock(String resource, Callable<T> callable) throws LockException;

}
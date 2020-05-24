package com.fanxuankai.zeus.mq.broker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.mq.broker.domain.Lock;

/**
 * @author fanxuankai
 */
public interface LockService extends IService<Lock> {

    /**
     * 加锁
     *
     * @param resource 资源
     * @return 加锁是否成功
     */
    boolean lock(String resource);

    /**
     * 释放锁
     *
     * @param resource 资源
     */
    void release(String resource);

    /**
     * 清理锁资源
     *
     * @param resource 资源
     * @param timeout  超时 ms
     */
    void clear(String resource, long timeout);
}

package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.domain.Lock;
import com.fanxuankai.zeus.mq.broker.mapper.LockMapper;
import com.fanxuankai.zeus.mq.broker.service.LockService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author fanxuankai
 */
@Component
public class LockServiceImpl extends ServiceImpl<LockMapper, Lock> implements LockService {

    @Override
    public boolean lock(String resource) {
        try {
            return save(new Lock()
                    .setResource(resource)
                    .setCreateDate(LocalDateTime.now()));
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public void release(String resource) {
        remove(new QueryWrapper<Lock>()
                .lambda()
                .eq(Lock::getResource, resource));
    }

    @Override
    public void clear(String resource, long timeout) {
        remove(new QueryWrapper<Lock>()
                .lambda()
                .eq(Lock::getResource, resource)
                .lt(Lock::getCreateDate,
                        LocalDateTime.now().plus(-timeout, ChronoUnit.MILLIS)));
    }
}

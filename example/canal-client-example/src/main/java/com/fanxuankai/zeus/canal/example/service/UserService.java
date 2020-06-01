package com.fanxuankai.zeus.canal.example.service;

import com.fanxuankai.zeus.aop.util.annotation.Log;
import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.data.redis.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
public class UserService {
    @Log
    @Lock(resource = "#user.id")
    public int doInLock(User user) {
        log.info("获取到锁并成功执行 " + user.getId());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}

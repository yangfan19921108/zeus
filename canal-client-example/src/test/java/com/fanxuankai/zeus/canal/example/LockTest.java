package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.canal.example.service.UserService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ForkJoinPool;

@SpringBootTest
public class LockTest {
    @Resource
    private UserService userService;

    @Test
    public void testLock() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.execute(() -> {
            User u = new User();
            u.setId(1L);
            userService.doInLock(u);
        });
        pool.execute(() -> {
            User u = new User();
            u.setId(2L);
            userService.doInLock(u);
        });
        pool.execute(() -> {
            User u = new User();
            u.setId(3L);
            userService.doInLock(u);
        });
    }
}

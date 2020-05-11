package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.canal.example.service.UserService;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@SpringBootTest
public class LockTest {
    @Resource
    private UserService userService;

    @Test
    public void testLock() {
        ExecutorService pool = ThreadPoolService.getInstance();
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

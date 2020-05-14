package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.canal.example.repository.UserRepository;
import com.fanxuankai.zeus.util.MockUtils;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class CanalClientExampleApplicationTests {
    @Resource
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        int startInclusive = 0;
        int endExclusive = 100000;
        Stopwatch sw = Stopwatch.createStarted();
        userRepository.batchSave(MockUtils.mock(startInclusive, endExclusive, User.class));
        sw.stop();
        log.info("Complete Init Data. {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
    }
}

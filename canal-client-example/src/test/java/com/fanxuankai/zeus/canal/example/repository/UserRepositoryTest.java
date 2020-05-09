package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.canal.example.util.MockUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {
    @Resource
    private UserRepository userRepository;

    @Test
    public void batchSave() {
        userRepository.batchSave(MockUtils.mock(0, 100000, User.class));
    }
}

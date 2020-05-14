package com.fanxuankai.zeus.canal.example.consumer;

import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.example.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
public class UserMqConsumer implements MqConsumer<User> {
    @Override
    public void insert(User user) {
        log.info("insert user");
    }

    @Override
    public void delete(User user) {
        log.info("delete user");
    }

    @Override
    public void update(User before, User after) {
        log.info("update user");
    }
}

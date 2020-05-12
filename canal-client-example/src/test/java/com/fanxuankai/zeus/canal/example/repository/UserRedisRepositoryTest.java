package com.fanxuankai.zeus.canal.example.repository;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.canal.client.redis.model.CombineKeyModel;
import com.fanxuankai.zeus.canal.client.redis.model.Entry;
import com.fanxuankai.zeus.canal.client.redis.model.UniqueKey;
import com.fanxuankai.zeus.canal.client.redis.model.UniqueKeyPro;
import com.fanxuankai.zeus.canal.example.domain.User;
import com.fanxuankai.zeus.canal.example.repository.redis.UserRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserRedisRepositoryTest {
    @Resource
    private UserRedisRepository userRedisRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        System.out.println(userRedisRepository.findAll()
                .stream()
                .allMatch(user -> Objects.equals(user.getDeleted(), 1)));

        System.out.println(userRedisRepository.findOne(new UniqueKey("phone", "458361630032396291")));

    }

    @Test
    public void count() {
        Long size = redisTemplate.opsForHash().size("fanxuankai.username:password");
        System.out.println(size);
    }

    @Test
    public void findAll() {
        List<User> all = userRedisRepository.findAll();
        log.info("{}", JSON.toJSONString(all, true));
    }

    @Test
    public void testAll() {
        CombineKeyModel ck = new CombineKeyModel(Arrays.asList(new Entry("username", "jCplH5C"), new Entry(
                "password",
                "rYR")));
        UniqueKey uk = new UniqueKey("phone", "Iw");
        UniqueKeyPro ukPro = new UniqueKeyPro("phone", Arrays.asList("QdWU", "cUsgBbeGN"));

        log.info("{}", userRedisRepository.count());
        log.info("{}", userRedisRepository.exists(uk));
        log.info("{}", userRedisRepository.exists(uk));
        log(userRedisRepository.existsById(458353649886691328L));
        log(userRedisRepository.getOne(uk));
        log(userRedisRepository.findById(458353649886691328L));
        userRedisRepository.findOne(uk).ifPresent(this::log);
        userRedisRepository.findOne(ck).ifPresent(this::log);
        log(userRedisRepository.findAll(ukPro));
        log(userRedisRepository.findAllById(Arrays.asList(458353649886691328L, 458353649895079936L)));
        userRedisRepository.findAll(Arrays.asList("username", "password")).forEach(this::log);
        userRedisRepository.findAll().forEach(this::log);
    }

    private void log(Object o) {
        log.info("{}", JSON.toJSONString(o, true));
    }
}

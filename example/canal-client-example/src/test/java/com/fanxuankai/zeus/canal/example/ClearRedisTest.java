package com.fanxuankai.zeus.canal.example;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootTest
public class ClearRedisTest {
    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Test
    public void clear() {
        Set<String> keys = redisTemplate.keys("*");
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    @Test
    public void testLong() {
        HashOperations<String, String, Long> hash = redisTemplate.opsForHash();
        hash.put("book", "effective", 100L);
        Object money = hash.get("book", "effective");
        System.out.println(money);
    }
}

package com.fanxuankai.zeus.canal.example.repository.redis;

import com.fanxuankai.zeus.canal.client.core.annotation.Filter;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import com.fanxuankai.zeus.canal.client.redis.repository.RedisRepository;
import com.fanxuankai.zeus.canal.example.domain.User;

/**
 * @author fanxuankai
 */
@CanalToRedis(filter = @Filter(aviatorExpression = "lastModifiedDate >= '2020-04-18 12:00:00:000'"))
public interface UserRedisRepository extends RedisRepository<User> {

}

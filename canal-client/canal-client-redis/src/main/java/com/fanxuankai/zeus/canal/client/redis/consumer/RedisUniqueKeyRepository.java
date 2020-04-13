package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.redis.model.UniqueKey;
import com.fanxuankai.zeus.canal.client.redis.model.UniqueKeyPro;

import java.util.List;
import java.util.Optional;

/**
 * Redis 唯一键查询 Repository
 *
 * @author fanxuankai
 */
public interface RedisUniqueKeyRepository<T> {

    /**
     * 查询
     *
     * @param uniqueKey UniqueKey
     * @return 有可能为empty
     */
    Optional<T> findOne(UniqueKey uniqueKey);

    /**
     * 判断是否存在
     *
     * @param uniqueKey UniqueKey
     * @return true or false
     */
    boolean exists(UniqueKey uniqueKey);

    /**
     * 查询所有
     *
     * @param uniqueKeyPro UniqueKeyPro
     * @return 有可能为empty
     */
    List<T> findAll(UniqueKeyPro uniqueKeyPro);

    /**
     * 查询
     *
     * @param uniqueKey UniqueKey
     * @return 有可能为null
     */
    T getOne(UniqueKey uniqueKey);
}

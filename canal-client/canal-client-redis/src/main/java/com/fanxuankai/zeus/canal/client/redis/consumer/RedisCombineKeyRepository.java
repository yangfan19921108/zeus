package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.redis.model.CombineKey;

import java.util.Optional;

/**
 * Redis 组合键查询 Repository 接口
 *
 * @author fanxuankai
 */
public interface RedisCombineKeyRepository<T> {

    /**
     * 查询所有
     *
     * @param combineKey 组合键
     * @return Optional 无数据返回 empty
     */
    Optional<T> findOne(CombineKey combineKey);

}

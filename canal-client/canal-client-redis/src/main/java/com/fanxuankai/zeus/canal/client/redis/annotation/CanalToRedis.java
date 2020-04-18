package com.fanxuankai.zeus.canal.client.redis.annotation;

import com.fanxuankai.zeus.canal.client.core.annotation.CombineKey;
import com.fanxuankai.zeus.canal.client.core.annotation.Filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis 消费配置
 *
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalToRedis {

    /**
     * 默认为 schema.table
     *
     * @return hash key
     */
    String key() default "";

    /**
     * id 是否作为 hashKey
     *
     * @return true or false
     */
    boolean idAsHashKey() default true;

    /**
     * hash key 增加 uniqueKey 后缀, 作为 hash 的集合名, 以 uniqueKey 的值作为 hash 的 field
     *
     * @return 唯一键
     */
    String[] uniqueKeys() default {};

    /**
     * hash key 增加 combineKeys 后缀, 作为 hash 的集合名, 以 combineKeys 的值作为 hash 的 field
     *
     * @return 组合键
     */
    CombineKey[] combineKeys() default {};

    /**
     * 过滤
     *
     * @return Filter
     */
    Filter filter() default @Filter;

}

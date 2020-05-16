package com.fanxuankai.zeus.canal.client.es.annotation;

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
public @interface CanalToEs {

    /**
     * 过滤
     *
     * @return Filter
     */
    Filter filter() default @Filter;

}

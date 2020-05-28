package com.fanxuankai.zeus.canal.client.mq.core.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.annotation.Filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.otter.canal.protocol.CanalEntry.EventType.*;

/**
 * MQ 消费配置
 *
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalToMq {

    /**
     * 默认为 schema.table.EventType, .EventType 后缀自动添加, 不需指定
     *
     * @return 队列名
     */
    String name() default "";

    /**
     * 要消费的事件类型, 默认为增、删、改
     *
     * @return 事件类型
     */
    CanalEntry.EventType[] eventTypes() default {INSERT, DELETE, UPDATE};

    /**
     * 过滤
     *
     * @return Filter
     */
    Filter filter() default @Filter;

    /**
     * 如果消费者具有幂等性, 建议开启可重复消费, MQ 吞吐量会有大大提升.
     *
     * @return true or false
     */
    boolean repeatableConsumption() default false;
}

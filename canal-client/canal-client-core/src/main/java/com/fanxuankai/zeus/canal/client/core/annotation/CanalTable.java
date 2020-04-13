package com.fanxuankai.zeus.canal.client.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalTable {
    /**
     * @return 数据库名
     */
    String schema() default "";

    /**
     * 如果使用了 javax.persistence.Table 注解, 取 @Table.value(), 否则取实体类名转下划线
     *
     * @return 数据库表名
     */
    String name() default "";
}

package com.fanxuankai.zeus.canal.client.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤
 *
 * @author fanxuankai
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    /**
     * 表达式必须返回 true or false
     *
     * @return Google Aviator 表达式
     */
    String aviatorExpression() default "";

    /**
     * 指定需要值发生变化的属性名
     *
     * @return 类属性名
     */
    String[] updatedFields() default {};
}

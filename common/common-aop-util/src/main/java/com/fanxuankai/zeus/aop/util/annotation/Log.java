package com.fanxuankai.zeus.aop.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 执行时间
     *
     * @return log the execute time
     */
    boolean executeTime() default true;

    /**
     * 参数
     *
     * @return log the params
     */
    boolean params() default true;

    /**
     * 返回值
     *
     * @return log the return value
     */
    boolean returnValue() default true;

}

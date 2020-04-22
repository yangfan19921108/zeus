package com.fanxuankai.zeus.common.aop.util.annotation;

import com.fanxuankai.zeus.common.aop.util.configuration.EnableAopInterceptorConfiguration;
import com.fanxuankai.zeus.common.aop.util.configuration.EnableAopInterceptorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EnableAopInterceptorRegistrar.class, EnableAopInterceptorConfiguration.class})
public @interface EnableAopInterceptor {

}

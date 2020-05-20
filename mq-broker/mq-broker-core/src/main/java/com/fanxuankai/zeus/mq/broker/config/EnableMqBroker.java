package com.fanxuankai.zeus.mq.broker.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({EnableMqBrokerImportRegistrar.class})
public @interface EnableMqBroker {

}

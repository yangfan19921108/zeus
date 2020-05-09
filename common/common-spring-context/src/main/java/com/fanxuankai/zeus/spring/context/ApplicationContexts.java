package com.fanxuankai.zeus.spring.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * @author fanxuankai
 */
public class ApplicationContexts implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static Resource getResource(String location) {
        return applicationContext.getResource(location);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContexts.applicationContext = applicationContext;
    }

}

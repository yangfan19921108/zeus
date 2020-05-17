package com.fanxuankai.zeus.spring.context;

import com.fanxuankai.zeus.util.TypeReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

/**
 * @author fanxuankai
 */
public class ApplicationContexts implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static Resource getResource(String location) {
        return applicationContext.getResource(location);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(TypeReference<T> type) {
        return applicationContext.getBean(type.getType());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        ApplicationContexts.applicationContext = applicationContext;
    }
}

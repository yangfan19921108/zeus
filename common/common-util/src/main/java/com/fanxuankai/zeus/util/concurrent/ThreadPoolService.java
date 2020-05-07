package com.fanxuankai.zeus.util.concurrent;

import com.fanxuankai.zeus.util.SystemProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池, 枚举方式实现单例模式
 *
 * @author fanxuankai
 */
@Slf4j
public class ThreadPoolService {

    private static final String PROPERTY_PREFIX = "com.fanxuankai.zeus";
    private static final String PROPERTY_CPS = PROPERTY_PREFIX + ".corePoolSize";
    private static final String PROPERTY_KAT = PROPERTY_PREFIX + ".keepAliveTime";

    private ThreadPoolService() {
    }

    public static ExecutorService getInstance() {
        return Singleton.INSTANCE.executorService;
    }

    private enum Singleton {
        // 实例
        INSTANCE;

        private final ExecutorService executorService;

        Singleton() {
            executorService = threadPoolExecutor();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shut down thread pool");
                executorService.shutdown();
            }));
        }

        private ThreadPoolExecutor threadPoolExecutor() {
            int corePoolSize = SystemProperties.getInteger(PROPERTY_CPS)
                    .orElse(Runtime.getRuntime().availableProcessors() * 2);
            return new ThreadPoolExecutor(corePoolSize, corePoolSize * 2,
                    SystemProperties.getLong(PROPERTY_KAT).orElse(60L), TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadFactoryBuilder().setNameFormat("ZeusThreadPool-worker-%d").build());
        }

    }

}

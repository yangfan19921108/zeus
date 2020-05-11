package com.fanxuankai.zeus.canal.client.core.config;

import com.alibaba.google.common.util.concurrent.ThreadFactoryBuilder;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Canal 工作者
 *
 * @author fanxuankai
 */
@Slf4j
public class CanalWorker implements ApplicationRunner {

    private final Config config;
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * 应用退出时应清除 canal running 标记
     */
    private boolean shouldClearTagWhenExit;
    /**
     * canal running Redis.key
     */
    private String key;
    private ScheduledFuture<?> scheduledFuture;

    public CanalWorker(Config config) {
        this.config = config;
        scheduledExecutor = new ScheduledThreadPoolExecutor(1,
                new ThreadFactoryBuilder().setNameFormat(CanalWorker.class.getSimpleName()
                        + " " + config.applicationInfo.uniqueString()).build());
    }

    @Override
    public void run(ApplicationArguments args) {
        key = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                config.applicationInfo.uniqueString() + CommonConstants.SEPARATOR + RedisConstants.CANAL_RUNNING_TAG);
        CanalConfig canalConfig = ApplicationContexts.getBean(CanalConfig.class);
        if (Objects.equals(canalConfig.getRetryStart(), Boolean.TRUE)) {
            scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
                if (retryStart()) {
                    scheduledFuture.cancel(true);
                    scheduledExecutor.shutdown();
                }
            }, 0, canalConfig.getRetryStartIntervalSeconds(), TimeUnit.SECONDS);
        } else {
            retryStart();
        }
    }

    private boolean retryStart() {
        if (!Boolean.TRUE.equals(RedisUtils.redisTemplate().opsForValue().setIfAbsent(key, true))) {
            log.info("{} Exists", key);
            return false;
        }
        log.info("{} Running...", key);
        shouldClearTagWhenExit = true;
        ThreadPoolService.getInstance().execute(config.otter::start);
        log.info("{} Start", key);
        return true;
    }

    @PreDestroy
    public void preDestroy() {
        config.otter.stop();
        if (shouldClearTagWhenExit) {
            RedisUtils.redisTemplate().delete(key);
            log.info("{} Canal stop", key);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Config {
        private final Otter otter;
        private final ApplicationInfo applicationInfo;
    }
}

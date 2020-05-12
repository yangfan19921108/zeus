package com.fanxuankai.zeus.canal.client.core.flow;

import com.alibaba.google.common.util.concurrent.ThreadFactoryBuilder;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
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
    private final Otter otter;
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
                        + " " + config.getApplicationInfo().uniqueString()).build());
        otter = new FlowOtter(config);
    }

    @Override
    public void run(ApplicationArguments args) {
        key = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                config.getApplicationInfo().uniqueString() + CommonConstants.SEPARATOR + RedisConstants.CANAL_RUNNING_TAG);
        CanalConfig canalConfig = config.getCanalConfig();
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
        if (!Boolean.TRUE.equals(config.getRedisTemplate().opsForValue().setIfAbsent(key, true))) {
            log.info("{} Exists", key);
            return false;
        }
        log.info("{} Running...", key);
        shouldClearTagWhenExit = true;
        ThreadPoolService.getInstance().execute(otter::start);
        log.info("{} Start", key);
        return true;
    }

    @PreDestroy
    public void preDestroy() {
        otter.stop();
        if (shouldClearTagWhenExit) {
            config.getRedisTemplate().delete(key);
            log.info("{} Canal stop", key);
        }
    }
}

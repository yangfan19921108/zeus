package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.protocol.Handler;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.util.ConsumeEntryLogger;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import com.fanxuankai.zeus.data.redis.ObjectRedisTemplate;
import com.fanxuankai.zeus.data.redis.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.spring.context.ApplicationContexts;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Message 处理器
 *
 * @author fanxuankai
 */
@Slf4j
public class MessageHandler implements Handler<MessageWrapper> {

    /**
     * 消费者信息
     */
    private final Config config;
    /**
     * logfile offset 消费标记
     */
    private final String logFileOffsetTag;
    private final CanalProperties canalProperties;
    private final ObjectRedisTemplate redisTemplate;

    public MessageHandler(Config config) {
        this.config = config;
        this.logFileOffsetTag = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                config.getApplicationInfo().uniqueString() + CommonConstants.SEPARATOR + RedisConstants.LOGFILE_OFFSET);
        this.canalProperties = ApplicationContexts.getBean(CanalProperties.class);
        this.redisTemplate = ApplicationContexts.getBean(ObjectRedisTemplate.class);
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        List<EntryWrapper> entryWrapperList = messageWrapper.getEntryWrapperList();
        if (CollectionUtils.isEmpty(entryWrapperList)) {
            return;
        }
        try {
            if (messageWrapper.getRowDataCountAfterFilter() >= canalProperties.getPerformanceThreshold()) {
                doHandlePerformance(entryWrapperList, messageWrapper.getBatchId());
            } else {
                doHandle(entryWrapperList, messageWrapper.getBatchId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes unchecked")
    private void doHandle(List<EntryWrapper> entryWrapperList, long batchId) {
        for (EntryWrapper entryWrapper : entryWrapperList) {
            MessageConsumer consumer = config.getConsumerMap().get(entryWrapper.getEventType());
            if (consumer == null
                    || !consumer.canProcess(entryWrapper)
                    || existsLogfileOffset(entryWrapper, batchId)) {
                continue;
            }
            Object process = consumer.process(entryWrapper);
            if (ObjectUtils.isEmpty(process)) {
                continue;
            }
            long time = consume(consumer, process, entryWrapper);
            logEntry(entryWrapper, batchId, time);
        }
    }

    @SuppressWarnings("rawtypes unchecked")
    private long consume(MessageConsumer consumer, Object process, EntryWrapper entryWrapper) {
        Stopwatch sw = Stopwatch.createStarted();
        consumer.consume(process);
        sw.stop();
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        putOffset(entryWrapper.getLogfileName(), entryWrapper.getLogfileOffset());
        return time;
    }

    @SuppressWarnings("rawtypes unchecked")
    private void doHandlePerformance(List<EntryWrapper> entryWrapperList, long batchId) throws Exception {
        // 异步处理
        ExecutorService executorService = ThreadPoolService.getInstance();
        List<Future<EntryWrapperProcess>> futureList = entryWrapperList.stream()
                .map(entryWrapper -> executorService.submit(() -> {
                    MessageConsumer consumer = config.getConsumerMap().get(entryWrapper.getEventType());
                    if (consumer == null
                            || !consumer.canProcess(entryWrapper)
                            || existsLogfileOffset(entryWrapper, batchId)) {
                        return new EntryWrapperProcess(entryWrapper, null, null);
                    }
                    return new EntryWrapperProcess(entryWrapper, consumer.process(entryWrapper), consumer);
                }))
                .collect(Collectors.toList());
        // 顺序消费
        for (Future<EntryWrapperProcess> future : futureList) {
            EntryWrapperProcess entryWrapperProcess = future.get();
            Object process = entryWrapperProcess.process;
            if (!ObjectUtils.isEmpty(process)) {
                EntryWrapper entryWrapper = entryWrapperProcess.entryWrapper;
                MessageConsumer consumer = entryWrapperProcess.consumer;
                long time = consume(consumer, process, entryWrapper);
                logEntry(entryWrapper, batchId, time);
            }
        }
    }

    private boolean existsLogfileOffset(EntryWrapper entryWrapper, long batchId) {
        String logfileName = entryWrapper.getLogfileName();
        long logfileOffset = entryWrapper.getLogfileOffset();
        if (existsLogfileOffset(logfileName, logfileOffset)) {
            log.info("防重消费 {} batchId: {} {}", entryWrapper.toString(), batchId,
                    config.getApplicationInfo().uniqueString());
            return true;
        }
        return false;
    }

    private void logEntry(EntryWrapper entryWrapper, long batchId, long time) {
        if (canalProperties.isShowEntryLog()) {
            ConsumeEntryLogger.asyncLog(ConsumeEntryLogger.LogInfo
                    .builder()
                    .canalProperties(canalProperties)
                    .applicationInfo(config.getApplicationInfo())
                    .entryWrapper(entryWrapper)
                    .batchId(batchId)
                    .time(time)
                    .build());
        }
    }

    private boolean existsLogfileOffset(String logfileName, long offset) {
        Object value = redisTemplate.opsForHash().get(logFileOffsetTag, logfileName);
        if (value == null) {
            return false;
        }
        return Long.parseLong(value.toString()) >= offset;
    }

    private void putOffset(String logfileName, long offset) {
        redisTemplate.opsForHash().put(logFileOffsetTag, logfileName, offset);
    }

    @AllArgsConstructor
    @Getter
    @SuppressWarnings("rawtypes")
    private static class EntryWrapperProcess {
        private final EntryWrapper entryWrapper;
        private final Object process;
        private final MessageConsumer consumer;
    }

}

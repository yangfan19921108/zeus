package com.fanxuankai.zeus.canal.client.core.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.aviator.Aviators;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.constants.RedisConstants;
import com.fanxuankai.zeus.canal.client.core.enums.RedisKeyPrefix;
import com.fanxuankai.zeus.canal.client.core.execption.HandleException;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.util.ConsumeEntryLogger;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Message 处理器
 *
 * @author fanxuankai
 */
@Slf4j
public class MessageHandler implements Handler<MessageWrapper>, InitializingBean {

    private final Config config;
    private String logFileOffsetTag;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CanalConfig canalConfig;

    public MessageHandler(Config config) {
        this.config = config;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        long l = System.currentTimeMillis();
        List<EntryWrapper> entryWrapperList = messageWrapper.getEntryWrapperList();
        entryWrapperList.removeIf(EntryWrapper::isDdl);
        int rowChangeDataCount = 0;
        if (!CollectionUtils.isEmpty(entryWrapperList)) {
            try {
                if (messageWrapper.getAllRawRowDataCount() >= canalConfig.getPerformanceThreshold()) {
                    rowChangeDataCount = doHandlePerformance(entryWrapperList, messageWrapper.getBatchId());
                } else {
                    rowChangeDataCount = doHandle(entryWrapperList, messageWrapper.getBatchId());
                }
            } catch (Exception e) {
                throw new HandleException(e);
            }
        }
        if (messageWrapper.getAllRawRowDataCount() > 0) {
            log.info("{} Consume batchId: {}, rowDataCount: {}({}), time: {}ms", config.applicationInfo.uniqueString(),
                    messageWrapper.getBatchId(),
                    rowChangeDataCount, messageWrapper.getAllRawRowDataCount(), System.currentTimeMillis() - l);
        }
    }

    @SuppressWarnings("rawtypes unchecked")
    private int doHandle(List<EntryWrapper> entryWrapperList, long batchId) {
        int rowChangeDataCount = 0;
        for (EntryWrapper entryWrapper : entryWrapperList) {
            if (existsLogfileOffset(entryWrapper, batchId)) {
                continue;
            }
            MessageConsumer consumer = config.consumerMap.get(entryWrapper.getEventType());
            if (consumer == null) {
                throw new HandleException("无消费者");
            }
            if (consumer.canProcess(entryWrapper)) {
                Object process = process(consumer, entryWrapper);
                if (ObjectUtils.isEmpty(process)) {
                    continue;
                }
                long time = consume(consumer, process, entryWrapper);
                rowChangeDataCount += entryWrapper.getAllRowDataList().size();
                logEntry(entryWrapper, batchId, time);
            }
        }
        return rowChangeDataCount;
    }

    @SuppressWarnings("rawtypes unchecked")
    private Object process(MessageConsumer consumer, EntryWrapper entryWrapper) {
        FilterMetadata filterMetadata = consumer.filter(entryWrapper);
        filterEntryRowData(entryWrapper, filterMetadata);
        return consumer.process(entryWrapper);
    }

    @SuppressWarnings("rawtypes unchecked")
    private long consume(MessageConsumer consumer, Object process, EntryWrapper entryWrapper) {
        long ll = System.currentTimeMillis();
        consumer.consume(process);
        long time = System.currentTimeMillis() - ll;
        putOffset(entryWrapper.getLogfileName(), entryWrapper.getLogfileOffset());
        return time;
    }

    @SuppressWarnings("rawtypes unchecked")
    private int doHandlePerformance(List<EntryWrapper> entryWrapperList, long batchId) throws Exception {
        // 异步处理
        List<Future<EntryWrapperProcess>> futureList = entryWrapperList.stream()
                .map(entryWrapper -> ForkJoinPool.commonPool().submit(() -> {
                    MessageConsumer consumer = config.consumerMap.get(entryWrapper.getEventType());
                    if (consumer == null) {
                        throw new HandleException("无消费者");
                    }
                    Object process = null;
                    if (!existsLogfileOffset(entryWrapper, batchId)
                            && consumer.canProcess(entryWrapper)) {
                        process = process(consumer, entryWrapper);
                    }
                    return new EntryWrapperProcess(entryWrapper, process, consumer);
                }))
                .collect(Collectors.toList());
        // 顺序消费
        int rowChangeDataCount = 0;
        for (Future<EntryWrapperProcess> future : futureList) {
            EntryWrapperProcess entryWrapperProcess = future.get();
            Object process = entryWrapperProcess.process;
            if (!ObjectUtils.isEmpty(process)) {
                EntryWrapper entryWrapper = entryWrapperProcess.entryWrapper;
                MessageConsumer consumer = entryWrapperProcess.consumer;
                long time = consume(consumer, process, entryWrapper);
                rowChangeDataCount += entryWrapper.getAllRowDataList().size();
                logEntry(entryWrapper, batchId, time);
            }
        }
        return rowChangeDataCount;
    }

    private void filterEntryRowData(EntryWrapper entryWrapper, FilterMetadata filterMetadata) {
        Class<?> domainType = CanalTableCache.getMetadata(entryWrapper).getDomainType();
        List<CanalEntry.RowData> rowDataList = entryWrapper.getAllRowDataList()
                .stream()
                .filter(rowData -> filterRowData(rowData, filterMetadata, domainType))
                .collect(Collectors.toList());
        entryWrapper.setAllRowDataList(rowDataList);
    }

    private boolean filterRowData(CanalEntry.RowData rowData, FilterMetadata filterMetadata, Class<?> domainType) {
        Map<String, CanalEntry.Column> beforeColumnMap = CommonUtils.toColumnMap(rowData.getBeforeColumnsList());
        Map<String, CanalEntry.Column> afterColumnMap = CommonUtils.toColumnMap(rowData.getAfterColumnsList());
        List<String> updatedFields = filterMetadata.getUpdatedFields();
        if (!CollectionUtils.isEmpty(updatedFields)) {
            // 新增或者修改
            if (!CollectionUtils.isEmpty(afterColumnMap)) {
                boolean allMatch = afterColumnMap.entrySet()
                        .stream()
                        .filter(entry -> updatedFields.contains(entry.getKey()))
                        .allMatch(entry -> {
                            CanalEntry.Column oldColumn = beforeColumnMap.get(entry.getKey());
                            return oldColumn == null || entry.getValue().getUpdated();
                        });
                if (!allMatch) {
                    return false;
                }
            }
            // 删除默认为已全部修改
        }
        String aviatorExpression = filterMetadata.getAviatorExpression();
        if (StringUtils.isNotBlank(aviatorExpression)) {
            // 新增或者修改
            if (!CollectionUtils.isEmpty(afterColumnMap)) {
                return Aviators.exec(CommonUtils.toMap(rowData.getAfterColumnsList()), aviatorExpression, domainType);
            }
            // 删除
            if (!CollectionUtils.isEmpty(beforeColumnMap)) {
                return Aviators.exec(CommonUtils.toMap(rowData.getBeforeColumnsList()), aviatorExpression,
                        domainType);
            }
        }
        return true;
    }

    private boolean existsLogfileOffset(EntryWrapper entryWrapper, long batchId) throws HandleException {
        String logfileName = entryWrapper.getLogfileName();
        long logfileOffset = entryWrapper.getLogfileOffset();
        if (existsLogfileOffset(logfileName, logfileOffset)) {
            ExistsLogfileOffset existsLogfileOffset = ExistsLogfileOffset.builder()
                    .name(config.applicationInfo.uniqueString())
                    .batchId(batchId)
                    .schema(entryWrapper.getSchemaName())
                    .table(entryWrapper.getTableName())
                    .logfileName(logfileName)
                    .logfileOffset(logfileOffset)
                    .build();
            log.info("防重消费: {}", JSON.toJSONString(existsLogfileOffset));
            return true;
        }
        return false;
    }

    private void logEntry(EntryWrapper entryWrapper, long batchId, long time) {
        if (Objects.equals(canalConfig.getShowLog(), Boolean.TRUE)) {
            ConsumeEntryLogger.asyncLog(new ConsumeEntryLogger.LogInfo(canalConfig,
                    config.applicationInfo, entryWrapper, batchId, time));
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

    @Override
    public void afterPropertiesSet() {
        this.logFileOffsetTag = RedisUtils.customKey(RedisKeyPrefix.SERVICE_CACHE,
                config.applicationInfo.uniqueString() + CommonConstants.SEPARATOR + RedisConstants.LOGFILE_OFFSET);
    }

    @SuppressWarnings("rawtypes")
    @AllArgsConstructor
    @Getter
    public static class Config {
        private final Map<CanalEntry.EventType, MessageConsumer> consumerMap;
        private final ApplicationInfo applicationInfo;
    }

    @Getter
    @Builder
    private static class ExistsLogfileOffset {
        private final String name;
        private final long batchId;
        private final String schema;
        private final String table;
        private final String logfileName;
        private final long logfileOffset;
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

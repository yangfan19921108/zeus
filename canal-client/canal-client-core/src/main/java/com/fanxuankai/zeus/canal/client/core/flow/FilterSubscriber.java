package com.fanxuankai.zeus.canal.client.core.flow;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.aviator.Aviators;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import com.fanxuankai.zeus.util.concurrent.Flow;
import com.fanxuankai.zeus.util.concurrent.SubmissionPublisher;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 过滤订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class FilterSubscriber extends SubmissionPublisher<ContextWrapper> implements Flow.Subscriber<ContextWrapper> {

    private final Otter otter;
    private final Config config;
    private Flow.Subscription subscription;

    public FilterSubscriber(Otter otter, Config config) {
        this.otter = otter;
        this.config = config;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(ContextWrapper item) {
        MessageWrapper messageWrapper = item.getMessageWrapper();
        if (!messageWrapper.getEntryWrapperList().isEmpty()) {
            long batchId = messageWrapper.getBatchId();
            Stopwatch sw = Stopwatch.createStarted();
            messageWrapper.getEntryWrapperList().forEach(this::filterEntryRowData);
            sw.stop();
            if (Objects.equals(config.getCanalConfig().getShowEventLog(), Boolean.TRUE)) {
                log.info("{} Filter batchId: {} rowDataCount: {} -> {} time: {}ms",
                        config.getApplicationInfo().uniqueString(), batchId,
                        messageWrapper.getRowDataCountBeforeFilter(),
                        messageWrapper.getRowDataCountAfterFilter(), sw.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        submit(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", config.getApplicationInfo().uniqueString(), throwable.getLocalizedMessage()),
                throwable);
        this.subscription.cancel();
        this.otter.stop();
    }

    @Override
    public void onComplete() {
        log.info("{} Done", config.getApplicationInfo().uniqueString());
    }

    @SuppressWarnings("unchecked rawtypes")
    private void filterEntryRowData(EntryWrapper entryWrapper) {
        MessageConsumer consumer = config.getConsumerMap().get(entryWrapper.getEventType());
        // 如果不能处理, 置空, return
        if (consumer == null || !consumer.canProcess(entryWrapper)) {
            entryWrapper.setAllRowDataList(Collections.emptyList());
            return;
        }
        // 如果不可过滤, 跳过不处理
        if (!consumer.filterable()) {
            return;
        }
        FilterMetadata filterMetadata = consumer.filter(entryWrapper);
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
        if (!filterRowDataWithUpdatedFields(beforeColumnMap, afterColumnMap, filterMetadata)) {
            return false;
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

    private boolean filterRowDataWithUpdatedFields(Map<String, CanalEntry.Column> beforeColumnMap,
                                                   Map<String, CanalEntry.Column> afterColumnMap,
                                                   FilterMetadata filterMetadata) {
        List<String> updatedFields = filterMetadata.getUpdatedFields();
        if (!CollectionUtils.isEmpty(updatedFields)) {
            // 只考虑新增或者修改, 删除默认为已全部修改
            if (!CollectionUtils.isEmpty(afterColumnMap)) {
                Stream<Map.Entry<String, CanalEntry.Column>> stream = afterColumnMap.entrySet()
                        .stream()
                        .filter(entry -> updatedFields.contains(entry.getKey()));
                Predicate<Map.Entry<String, CanalEntry.Column>> predicate = entry -> {
                    CanalEntry.Column oldColumn = beforeColumnMap.get(entry.getKey());
                    return oldColumn == null || entry.getValue().getUpdated();
                };
                if (filterMetadata.isAnyFieldMatch()) {
                    return stream.anyMatch(predicate);
                }
                return stream.allMatch(predicate);
            }
        }
        return true;
    }
}

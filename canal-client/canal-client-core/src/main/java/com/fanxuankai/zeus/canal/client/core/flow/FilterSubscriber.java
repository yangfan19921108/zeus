package com.fanxuankai.zeus.canal.client.core.flow;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.aviator.Aviators;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableCache;
import com.fanxuankai.zeus.canal.client.core.metadata.FilterMetadata;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.MessageConsumer;
import com.fanxuankai.zeus.canal.client.core.protocol.Otter;
import com.fanxuankai.zeus.canal.client.core.util.CommonUtils;
import com.fanxuankai.zeus.canal.client.core.wrapper.ContextWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

/**
 * 过滤订阅者
 *
 * @author fanxuankai
 */
@Slf4j
public class FilterSubscriber extends SubmissionPublisher<ContextWrapper> implements Flow.Subscriber<ContextWrapper> {

    private final ConsumerInfo consumerInfo;
    private Otter otter;
    private Flow.Subscription subscription;

    public FilterSubscriber(Otter otter, ConsumerInfo consumerInfo) {
        this.otter = otter;
        this.consumerInfo = consumerInfo;
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
            long l = System.currentTimeMillis();
            messageWrapper.setEntryWrapperList(messageWrapper.getEntryWrapperList()
                    .stream()
                    .peek(this::filterEntryRowData)
                    .collect(Collectors.toList())
            );
            long l1 = System.currentTimeMillis() - l;
            log.info("{} Filter batchId: {} rowDataCount: {} -> {} time: {}ms",
                    consumerInfo.getApplicationInfo().uniqueString(), batchId,
                    messageWrapper.getRowDataCountBeforeFilter(),
                    messageWrapper.getRowDataCountAfterFilter(), l1);
        }
        submit(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("%s %s", consumerInfo.getApplicationInfo().uniqueString(),
                throwable.getLocalizedMessage()), throwable);
        this.subscription.cancel();
        this.otter.stop();
    }

    @Override
    public void onComplete() {
        log.info("{} Done", consumerInfo.getApplicationInfo().uniqueString());
    }

    @SuppressWarnings("unchecked rawtypes")
    private void filterEntryRowData(EntryWrapper entryWrapper) {
        MessageConsumer consumer = consumerInfo.getConsumerMap().get(entryWrapper.getEventType());
        // 如果不能处理, 置空, return
        if (!consumer.canProcess(entryWrapper)) {
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

}

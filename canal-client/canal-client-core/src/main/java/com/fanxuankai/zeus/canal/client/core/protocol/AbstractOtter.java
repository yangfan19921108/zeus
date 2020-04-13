package com.fanxuankai.zeus.canal.client.core.protocol;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.util.CanalConnectorHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alibaba.otter.canal.protocol.CanalEntry.EventType.*;

/**
 * Otter 客户端抽象类
 *
 * @author fanxuankai
 */
@Slf4j
public abstract class AbstractOtter implements Otter {

    /**
     * 过滤的事件类型
     */
    private static final List<CanalEntry.EventType> EVENT_TYPES = Arrays.asList(INSERT, DELETE, UPDATE, ERASE);
    private final ConnectConfig connectConfig;
    private volatile boolean running;

    @Resource
    private CanalConfig canalConfig;

    public AbstractOtter(ConnectConfig connectConfig) {
        this.connectConfig = connectConfig;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        // CanalConnector 传给 subscriber 消费后再提交
        String subscriberName = connectConfig.getApplicationInfo().uniqueString();
        ForkJoinPool.commonPool().execute(() -> {
            try {
                CanalConnectorHolder.connect(connectConfig, canalConfig);
                running = true;
                while (running) {
                    try {
                        // 获取指定数量的数据
                        CanalConnector canalConnector = CanalConnectorHolder.get();
                        Message message = canalConnector.getWithoutAck(canalConfig.getBatchSize());
                        message.setEntries(filter(message.getEntries()));
                        long batchId = message.getId();
                        if (batchId != -1) {
                            if (!message.getEntries().isEmpty()) {
                                log.info("{} Get batchId: {}", subscriberName, batchId);
                            }
                            process(new Context(canalConnector, message));
                        }
                    } catch (CanalClientException e) {
                        log.error(String.format("%s Stop get data %s", subscriberName, e.getLocalizedMessage()), e);
                        CanalConnectorHolder.reconnect(connectConfig, canalConfig);
                        log.info("{} Start get data", subscriberName);
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(canalConfig.getIntervalMillis());
                    } catch (InterruptedException e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            } finally {
                CanalConnectorHolder.disconnect();
                log.info("{} Stop get data", subscriberName);
            }
        });
    }

    /**
     * 只消费增、删、改、删表事件，其它事件暂不支持且会被忽略
     *
     * @param entries CanalEntry.Entry
     */
    private List<CanalEntry.Entry> filter(List<CanalEntry.Entry> entries) {
        if (CollectionUtils.isEmpty(entries)) {
            return Collections.emptyList();
        }
        return entries.stream()
                .filter(entry -> entry.getEntryType() != CanalEntry.EntryType.TRANSACTIONBEGIN)
                .filter(entry -> entry.getEntryType() != CanalEntry.EntryType.TRANSACTIONEND)
                .filter(entry -> EVENT_TYPES.contains(entry.getHeader().getEventType()))
                .collect(Collectors.toList());
    }

}

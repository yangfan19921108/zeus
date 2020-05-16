package com.fanxuankai.zeus.canal.client.core.protocol;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.Context;
import com.fanxuankai.zeus.canal.client.core.util.CanalConnectorHolder;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final CanalProperties canalProperties;
    private volatile boolean running;

    public AbstractOtter(ConnectConfig connectConfig, CanalProperties canalProperties) {
        this.connectConfig = connectConfig;
        this.canalProperties = canalProperties;
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
        running = true;
        // CanalConnector 传给 subscriber 消费后再提交
        String subscriberName = connectConfig.getApplicationInfo().uniqueString();
        try {
            CanalConnectorHolder.connect(connectConfig, canalProperties);
            while (running) {
                try {
                    // 获取指定数量的数据
                    CanalConnector canalConnector = CanalConnectorHolder.get();
                    Stopwatch sw = Stopwatch.createStarted();
                    Message message;
                    if (canalProperties.getTimeoutMillis() == null) {
                        message = canalConnector.getWithoutAck(canalProperties.getBatchSize());
                    } else {
                        message = canalConnector.getWithoutAck(canalProperties.getBatchSize(),
                                canalProperties.getTimeoutMillis(), TimeUnit.MILLISECONDS);
                    }
                    sw.stop();
                    message.setEntries(filter(message.getEntries()));
                    long batchId = message.getId();
                    if (batchId != -1) {
                        if (Objects.equals(canalProperties.getShowEventLog(), Boolean.TRUE)
                                && !message.getEntries().isEmpty()) {
                            log.info("{} Get batchId: {} time: {}ms", subscriberName, batchId,
                                    sw.elapsed(TimeUnit.MILLISECONDS));
                        }
                        process(new Context(canalConnector, message));
                    }
                } catch (CanalClientException e) {
                    log.error(String.format("%s Stop Get Data %s", subscriberName, e.getLocalizedMessage()), e);
                    CanalConnectorHolder.reconnect(connectConfig, canalProperties);
                    log.info("{} Start Get Data", subscriberName);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(canalProperties.getIntervalMillis());
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        } finally {
            CanalConnectorHolder.disconnect();
            log.info("{} Stop get data", subscriberName);
        }
    }

    /**
     * 处理
     *
     * @param context 上下文
     */
    protected abstract void process(Context context);

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

package com.fanxuankai.zeus.canal.client.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.otter.canal.protocol.CanalEntry.EventType.*;

/**
 * @author fanxuankai
 */
@Slf4j
public class ConsumeEntryLogger {

    public static void asyncLog(LogInfo logInfo) {
        ThreadPoolService.getInstance().execute(() -> {
            EntryWrapper entryWrapper = logInfo.entryWrapper;
            LogRowChange logRowChange = new LogRowChange(logInfo.applicationInfo.uniqueString(), logInfo.batchId,
                    logInfo.time, entryWrapper);
            CanalProperties canalProperties = logInfo.getCanalProperties();
            if (canalProperties.isShowRowChange()) {
                List<List<LogColumn>> list = entryWrapper.getAllRowDataList().stream()
                        .map(o -> logColumns(o, entryWrapper.getEventType()))
                        .collect(Collectors.toList());
                log.info("{}\n{}", logRowChange.toString(), JSON.toJSONString(list,
                        canalProperties.isFormatRowChangeLog()));
            } else {
                log.info("{}", logRowChange.toString());
            }
        });
    }

    private static List<LogColumn> logColumns(CanalEntry.RowData rowData, CanalEntry.EventType eventType) {
        if (eventType == DELETE || eventType == ERASE) {
            return rowData.getBeforeColumnsList().stream()
                    .map(column -> new LogColumn(column.getName(), column.getValue(), null, false))
                    .collect(Collectors.toList());
        } else if (eventType == INSERT) {
            return rowData.getAfterColumnsList().stream()
                    .map(column -> new LogColumn(column.getName(), null, column.getValue(), true))
                    .collect(Collectors.toList());
        } else if (eventType == UPDATE) {
            List<LogColumn> logColumns = new ArrayList<>(rowData.getAfterColumnsCount());
            for (int i = 0; i < rowData.getAfterColumnsList().size(); i++) {
                CanalEntry.Column bColumn = rowData.getBeforeColumnsList().get(i);
                CanalEntry.Column aColumn = rowData.getAfterColumnsList().get(i);
                logColumns.add(new LogColumn(aColumn.getName(), bColumn.getValue(), aColumn.getValue(),
                        aColumn.getUpdated()));
            }
            return logColumns;
        }
        return Collections.emptyList();
    }

    @Builder
    @Getter
    public static class LogInfo {
        private final CanalProperties canalProperties;
        private final ApplicationInfo applicationInfo;
        private final EntryWrapper entryWrapper;
        private final long batchId;
        private final long time;
    }

    @Builder
    @Getter
    private static class LogRowChange {
        private final String name;
        private final long batchId;
        private final long time;
        private final EntryWrapper entryWrapper;

        @Override
        public String toString() {
            return String.format("%s batchId: %s time: %sms %s", entryWrapper.toString(), batchId, time, name);
        }
    }

    @Builder
    @Getter
    private static class LogColumn {
        private final String name;
        private final String oldValue;
        private final String value;
        private final boolean updated;
    }
}

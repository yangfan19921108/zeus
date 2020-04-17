package com.fanxuankai.zeus.canal.client.core.wrapper;

import com.alibaba.otter.canal.protocol.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
public class MessageWrapper {
    private final Message raw;
    @Getter
    @Setter
    private List<EntryWrapper> entryWrapperList;
    @Getter
    private final int rowDataCountBeforeFilter;

    public MessageWrapper(Message raw) {
        this.raw = raw;
        this.entryWrapperList = raw.getEntries().stream().map(EntryWrapper::new).collect(Collectors.toList());
        this.rowDataCountBeforeFilter = getRowDataCountAfterFilter();
    }

    public long getBatchId() {
        return raw.getId();
    }

    public int getRowDataCountAfterFilter() {
        return this.entryWrapperList
                .stream()
                .map(EntryWrapper::getRawRowDataCount)
                .reduce(Integer::sum)
                .orElse(0);
    }
}

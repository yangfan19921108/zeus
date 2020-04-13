package com.fanxuankai.zeus.canal.client.core.wrapper;

import com.fanxuankai.zeus.canal.client.core.model.Context;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fanxuankai
 */
public class ContextWrapper {

    private final Context raw;

    @Getter
    private final MessageWrapper messageWrapper;

    @Setter
    private boolean handleError;

    public ContextWrapper(Context raw) {
        this.raw = raw;
        this.messageWrapper = new MessageWrapper(raw.getMessage());
    }

    public void confirm() {
        if (handleError) {
            raw.rollback();
        } else {
            raw.ack();
        }
    }

}
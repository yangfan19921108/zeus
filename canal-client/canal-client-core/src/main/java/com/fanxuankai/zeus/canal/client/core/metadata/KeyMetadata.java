package com.fanxuankai.zeus.canal.client.core.metadata;

import com.google.common.base.CaseFormat;
import lombok.Getter;

/**
 * @author fanxuankai
 */
@Getter
public class KeyMetadata {
    private final String value;

    public KeyMetadata(String key) {
        this.value = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
    }
}

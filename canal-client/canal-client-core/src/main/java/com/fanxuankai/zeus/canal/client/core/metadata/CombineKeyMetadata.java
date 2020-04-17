package com.fanxuankai.zeus.canal.client.core.metadata;

import com.fanxuankai.zeus.canal.client.core.annotation.CombineKey;
import com.google.common.base.CaseFormat;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Getter
public class CombineKeyMetadata {
    private final List<String> values;

    public CombineKeyMetadata(CombineKey combineKey) {
        this.values = Arrays.stream(combineKey.values())
                .map(s -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s))
                .collect(Collectors.toList());
    }
}

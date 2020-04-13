package com.fanxuankai.zeus.canal.client.core.metadata;

import com.fanxuankai.zeus.canal.client.core.annotation.Filter;
import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter 注解元数据
 *
 * @author fanxuankai
 */
@Getter
@AllArgsConstructor
public class FilterMetadata {
    private String aviatorExpression = "";
    private List<String> updatedFields = Collections.emptyList();

    public FilterMetadata(Filter filter) {
        this.aviatorExpression = filter.aviatorExpression();
        this.updatedFields = Arrays.stream(filter.updatedFields())
                .map(s -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s))
                .collect(Collectors.toList());
    }

    public FilterMetadata() {
    }
}

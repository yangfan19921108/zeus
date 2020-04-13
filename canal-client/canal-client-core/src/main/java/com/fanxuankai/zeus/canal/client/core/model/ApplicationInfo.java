package com.fanxuankai.zeus.canal.client.core.model;

import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用唯一标识
 * name: 名称
 * behavior: 行为
 *
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class ApplicationInfo {
    private final String name;
    private final String behavior;

    public String uniqueString() {
        return name + CommonConstants.SEPARATOR + behavior;
    }
}

package com.fanxuankai.zeus.canal.client.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class Entry {
    private final String name;
    private final Object value;
}

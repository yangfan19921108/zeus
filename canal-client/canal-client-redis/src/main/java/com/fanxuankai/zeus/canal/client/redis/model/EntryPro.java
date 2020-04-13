package com.fanxuankai.zeus.canal.client.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class EntryPro {
    private final String name;
    private final List<Object> values;
}

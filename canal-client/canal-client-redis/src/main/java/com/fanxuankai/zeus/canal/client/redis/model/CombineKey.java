package com.fanxuankai.zeus.canal.client.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class CombineKey {
    private final List<Entry> entries;
}

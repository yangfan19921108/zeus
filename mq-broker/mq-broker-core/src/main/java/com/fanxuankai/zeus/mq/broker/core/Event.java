package com.fanxuankai.zeus.mq.broker.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanxuankai
 */
@Data
@AllArgsConstructor
public class Event implements Serializable {
    private String name;
    private String key;
    private String data;
}

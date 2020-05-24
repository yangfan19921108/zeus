package com.fanxuankai.zeus.mq.broker.xxl;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.consume.AbstractMqConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fanxuankai
 */
@Slf4j
public class XxlMqConsumer extends AbstractMqConsumer<String> {

    @Override
    public Event apply(String s) {
        return JSON.parseObject(s, Event.class);
    }

}

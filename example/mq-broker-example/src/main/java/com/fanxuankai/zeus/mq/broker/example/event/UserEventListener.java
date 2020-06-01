package com.fanxuankai.zeus.mq.broker.example.event;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.consume.EventListener;
import com.fanxuankai.zeus.mq.broker.core.consume.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fanxuankai
 */
@Slf4j
@Service
@Listener(event = "user0")
public class UserEventListener implements EventListener {

    @Override
    public void onEvent(Event event) {
        log.info(JSON.toJSONString(event));
    }
}

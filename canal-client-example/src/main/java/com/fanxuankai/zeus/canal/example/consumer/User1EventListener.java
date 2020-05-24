package com.fanxuankai.zeus.canal.example.consumer;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.consume.EventListener;
import com.fanxuankai.zeus.mq.broker.core.consume.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
@Listener(event = "user1")
public class User1EventListener implements EventListener {

    @Override
    public void onEvent(Event event) {
    }
}

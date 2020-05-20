package com.fanxuankai.zeus.canal.example.consumer;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
@Listener(event = "user")
public class UserEventListener1 implements EventListener {
    private final LongAdder longAdder = new LongAdder();

    @Override
    public void onEvent(Event event) {
        longAdder.increment();
        log.info(longAdder.toString());
    }
}

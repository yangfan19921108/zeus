package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EventPublisherTest {

    @Resource
    private EventPublisher eventPublisher;

    @Test
    public void publish() {
        IntStream.range(0, 10)
                .mapToObj(value -> "user" + value)
                .forEach(s -> eventPublisher.publish(IntStream.range(0, 10000)
                        .mapToObj(value -> new Event()
                                .setName(s)
                                .setKey(UUID.randomUUID().toString())
                                .setData("fanxuankai"))
                        .collect(Collectors.toList())));
    }
}

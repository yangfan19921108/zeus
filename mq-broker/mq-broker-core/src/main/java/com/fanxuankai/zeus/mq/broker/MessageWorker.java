package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.enums.MessageType;
import com.fanxuankai.zeus.mq.broker.mapper.MqBrokerMessageMapper;
import com.fanxuankai.zeus.mq.broker.service.MqBrokerMessageService;
import com.fanxuankai.zeus.util.concurrent.Threads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author fanxuankai
 */
@Slf4j
@Component
public class MessageWorker implements ApplicationRunner, DisposableBean {
    private final Random r = new Random();
    @Resource
    private MqBrokerProperties mqBrokerProperties;
    @Resource
    private MqBrokerMessageService mqBrokerMessageService;
    @Resource
    private MessageSendConsumer messageSendConsumer;
    @Resource
    private MessageReceiveConsumerFactory messageReceiveConsumerFactory;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private MqBrokerMessageMapper mqBrokerMessageMapper;
    private volatile boolean running;

    @Override
    public void run(ApplicationArguments args) {
        running = true;
        EventListenerFactory.getListeners()
                .forEach(listener -> {
                    threadPoolExecutor.execute(newRunnable(listener.event(), MessageType.SEND,
                            messageSendConsumer));
                    threadPoolExecutor.execute(newRunnable(listener.event(), MessageType.RECEIVE,
                            mqBrokerMessage -> messageReceiveConsumerFactory.get(mqBrokerMessage).accept(mqBrokerMessage)));
                });
    }

    private Runnable newRunnable(String event, MessageType messageType,
                                 Consumer<MqBrokerMessage> consumer) {
        return () -> {
            while (running) {
                List<MqBrokerMessage> lockData = Collections.emptyList();
                boolean lockFailure = false;
                try {
                    lockData = mqBrokerMessageService.getAndLock(event,
                            mqBrokerProperties.getBatchCount(),
                            mqBrokerProperties.getMaxRetry(), messageType);
                } catch (LockFailureException e) {
                    lockFailure = true;
                }
                for (MqBrokerMessage o : lockData) {
                    try {
                        consumer.accept(o);
                        mqBrokerMessageService.setSuccess(o);
                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage(), e);
                        o.setRetry(o.getRetry() + 1);
                        o.setError(e.getLocalizedMessage())
                                .setLastModifiedDate(LocalDateTime.now());
                        if (o.getRetry() >= mqBrokerProperties.getMaxRetry()) {
                            mqBrokerMessageService.setFailure(o);
                        } else {
                            mqBrokerMessageMapper.updateById(o.setStatus(Status.CREATED.getCode()));
                        }
                        break;
                    }
                }
                long sleep = mqBrokerProperties.getIntervalMillis() + r.nextInt(100);
                sleep = lockFailure ? Math.max(1000 * 30, sleep) : sleep;
                Threads.sleep(sleep, TimeUnit.MILLISECONDS);
            }
        };
    }

    @Override
    public void destroy() {
        running = false;
    }
}

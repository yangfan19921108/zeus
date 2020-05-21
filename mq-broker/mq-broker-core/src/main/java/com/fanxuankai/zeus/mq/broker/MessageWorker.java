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
                    log.warn(e.getLocalizedMessage());
                    lockFailure = true;
                }
                int size = lockData.size();
                int currentIndex;
                for (currentIndex = 0; currentIndex < size; currentIndex++) {
                    MqBrokerMessage o = lockData.get(currentIndex);
                    try {
                        consumer.accept(o);
                    } catch (Exception e) {
                        log.error(messageType + " consume error", e);
                        o.setRetry(o.getRetry() + 1)
                                .setError(e.toString())
                                .setLastModifiedDate(LocalDateTime.now());
                        if (o.getRetry() >= mqBrokerProperties.getMaxRetry()) {
                            mqBrokerMessageService.setFailure(o);
                        } else {
                            mqBrokerMessageMapper.updateById(o.setStatus(Status.CREATED.getCode()));
                        }
                        break;
                    }
                }
                if (currentIndex < size - 1) {
                    // 未处理的全部解锁
                    mqBrokerMessageService.unlock(lockData.subList(currentIndex + 1, size));
                }
                long sleep = mqBrokerProperties.getIntervalMillis() + r.nextInt(100);
                sleep = lockFailure ? Math.max(1000 * 5, sleep) : sleep;
                Threads.sleep(sleep, TimeUnit.MILLISECONDS);
            }
        };
    }

    @Override
    public void destroy() {
        running = false;
    }
}

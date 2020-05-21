package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MessageReceiveMapper;
import com.fanxuankai.zeus.mq.broker.service.MessageReceiveService;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author fanxuankai
 */
@Slf4j
@Component
public class MessageReceiveRunner implements ApplicationRunner {
    @Resource
    private MessageReceiveMapper messageReceiveMapper;
    @Resource
    private MqBrokerProperties mqBrokerProperties;
    @Resource
    private MessageReceiveService messageReceiveService;
    @Resource
    private MessageReceiveConsumerFactory messageReceiveConsumerFactory;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void run(ApplicationArguments args) {
        Random r = new Random();
        EventListenerFactory.getListeners()
                .stream()
                .map(listener -> (Runnable) () -> {
                    while (true) {
                        List<MessageReceive> lockData = Collections.emptyList();
                        boolean lockFailure = false;
                        try {
                            lockData = messageReceiveService.getAndLock(listener.event(),
                                    mqBrokerProperties.getConsumeBatchCount(),
                                    mqBrokerProperties.getMaxRetry());
                        } catch (LockFailureException e) {
                            lockFailure = true;
                        }
                        for (MessageReceive o : lockData) {
                            try {
                                messageReceiveConsumerFactory.get(o).accept(o);
                                messageReceiveService.setSuccess(o);
                            } catch (Exception e) {
                                o.setRetry(o.getRetry() + 1);
                                log.error(e.getLocalizedMessage(), e);
                                o.setError(e.getLocalizedMessage())
                                        .setLastModifiedDate(LocalDateTime.now());
                                if (o.getRetry() >= mqBrokerProperties.getMaxRetry()) {
                                    messageReceiveService.setFailure(o);
                                } else {
                                    messageReceiveMapper.updateById(o.setStatus(Status.CREATED.getCode()));
                                }
                                break;
                            }
                        }
                        try {
                            long sleep = mqBrokerProperties.getProduceIntervalMillis() + r.nextInt(100);
                            sleep = lockFailure ? Math.max(1000 * 30, sleep) : sleep;
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        } catch (InterruptedException e) {
                            log.warn(e.getLocalizedMessage(), e);
                            break;
                        }
                    }
                })
                .forEach(runnable -> threadPoolExecutor.execute(runnable));
    }
}

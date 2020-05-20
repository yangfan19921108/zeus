package com.fanxuankai.zeus.mq.broker;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.MessageSendConsumer;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;
import com.fanxuankai.zeus.mq.broker.mapper.MessageSendMapper;
import com.fanxuankai.zeus.mq.broker.service.MessageSendService;
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
public class MessageSendRunner implements ApplicationRunner {
    @Resource
    private MqBrokerProperties mqBrokerProperties;
    @Resource
    private MessageSendService messageSendService;
    @Resource
    private EventListenerFactory eventListenerFactory;
    @Resource
    private MessageSendConsumer messageSendConsumer;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private MessageSendMapper messageSendMapper;

    @Override
    public void run(ApplicationArguments args) {
        Random r = new Random();
        eventListenerFactory.getAllListener()
                .stream()
                .map(listener -> (Runnable) () -> {
                    while (true) {
                        try {
                            List<MessageSend> lockData = Collections.emptyList();
                            boolean lockFailure = false;
                            try {
                                lockData = messageSendService.getAndLock(listener.event(),
                                        mqBrokerProperties.getProduceBatchCount(),
                                        mqBrokerProperties.getMaxRetry());
                            } catch (LockFailureException e) {
                                lockFailure = true;
                            }
                            for (MessageSend o : lockData) {
                                try {
                                    messageSendConsumer.accept(o);
                                    messageSendService.setSuccess(o);
                                } catch (Exception e) {
                                    log.error(e.getLocalizedMessage(), e);
                                    o.setRetry(o.getRetry() + 1);
                                    o.setError(e.getLocalizedMessage())
                                            .setLastModifiedDate(LocalDateTime.now());
                                    if (o.getRetry() >= mqBrokerProperties.getMaxRetry()) {
                                        messageSendService.setFailure(o);
                                    } else {
                                        messageSendMapper.updateById(o.setStatus(Status.CREATED.getCode()));
                                    }
                                    break;
                                }
                            }
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

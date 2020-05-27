package com.fanxuankai.zeus.mq.broker.task;

import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.produce.MqProducer;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import com.fanxuankai.zeus.util.ThrowableUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fanxuankai
 */
@Slf4j
@Component
public class MsgSendTask implements Runnable {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private MsgSendService msgSendService;
    @Resource
    private MqProducer<MsgSend> mqProducer;
    @Resource
    private MqBrokerProperties mqBrokerProperties;

    @Override
    public void run() {
        while (true) {
            List<MsgSend> records = msgSendService.pullData();
            if (records.isEmpty()) {
                return;
            }
            int size = records.size() / mqBrokerProperties.getMaxConcurrent();
            size = size == 0 ? records.size() : size;
            List<List<MsgSend>> partition = Lists.partition(records, size);
            CountDownLatch countDownLatch = new CountDownLatch(partition.size());
            partition.forEach(list -> threadPoolExecutor.execute(() -> {
                try {
                    for (MsgSend msg : list) {
                        if (!msgSendService.lock(msg.getId())) {
                            continue;
                        }
                        try {
                            mqProducer.produce(msg);
                        } catch (Exception e) {
                            log.error("produce error", e);
                            msgSendService.failure(msg, ThrowableUtils.getStackTrace(e));
                        }
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }));
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error("计数器被中断", e);
            }
        }
    }
}

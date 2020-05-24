package com.fanxuankai.zeus.mq.broker.task;

import com.fanxuankai.zeus.mq.broker.core.produce.MqProducer;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.service.LockService;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import com.fanxuankai.zeus.util.ThrowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import static com.fanxuankai.zeus.mq.broker.constants.LockResourceConstants.MSG_SEND_TASK;

/**
 * @author fanxuankai
 */
@Slf4j
@Component
public class MsgSendTask implements Runnable {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private LockService lockService;
    @Resource
    private MsgSendService msgSendService;
    @Resource
    private MqProducer<MsgSend> mqProducer;

    @Override
    public void run() {
        if (!lockService.lock(MSG_SEND_TASK)) {
            return;
        }
        try {
            List<MsgSend> data = msgSendService.pullData();
            if (data.isEmpty()) {
                return;
            }
            Map<String, List<MsgSend>> grouped = new LinkedHashMap<>();
            for (MsgSend msg : data) {
                grouped.computeIfAbsent(msg.getTopic(), s -> new ArrayList<>()).add(msg);
            }
            CountDownLatch countDownLatch = new CountDownLatch(grouped.size());
            for (Map.Entry<String, List<MsgSend>> entry : grouped.entrySet()) {
                List<MsgSend> msgList = entry.getValue();
                threadPoolExecutor.execute(() -> {
                    for (MsgSend msg : msgList) {
                        try {
                            mqProducer.produce(msg);
                        } catch (Exception e) {
                            log.error("produce error", e);
                            msgSendService.failure(msg, ThrowableUtils.getStackTrace(e));
                            break;
                        }
                    }
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("消息处理被中断", e);
        } finally {
            lockService.release(MSG_SEND_TASK);
        }
    }
}

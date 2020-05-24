package com.fanxuankai.zeus.mq.broker.task;

import com.fanxuankai.zeus.mq.broker.core.consume.EventDistributorFactory;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;
import com.fanxuankai.zeus.mq.broker.service.LockService;
import com.fanxuankai.zeus.mq.broker.service.MsgReceiveService;
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

import static com.fanxuankai.zeus.mq.broker.constants.LockResourceConstants.MSG_RECEIVE_TASK;

/**
 * @author fanxuankai
 */
@Slf4j
@Component
public class MsgReceiveTask implements Runnable {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private LockService lockService;
    @Resource
    private MsgReceiveService msgReceiveService;
    @Resource
    private EventDistributorFactory eventDistributorFactory;

    @Override
    public void run() {
        if (!lockService.lock(MSG_RECEIVE_TASK)) {
            return;
        }
        try {
            while (true) {
                List<MsgReceive> data = msgReceiveService.pullData();
                if (data.isEmpty()) {
                    return;
                }
                Map<String, List<MsgReceive>> grouped = new LinkedHashMap<>();
                for (MsgReceive msg : data) {
                    grouped.computeIfAbsent(msg.getTopic(), s -> new ArrayList<>()).add(msg);
                }
                CountDownLatch countDownLatch = new CountDownLatch(grouped.size());
                for (Map.Entry<String, List<MsgReceive>> entry : grouped.entrySet()) {
                    List<MsgReceive> msgList = entry.getValue();
                    threadPoolExecutor.execute(() -> {
                        for (MsgReceive msg : msgList) {
                            try {
                                eventDistributorFactory.get(msg).accept(msg);
                            } catch (Exception e) {
                                log.error("distribute error", e);
                                msgReceiveService.unconsumed(msg, ThrowableUtils.getStackTrace(e));
                                break;
                            }
                        }
                        countDownLatch.countDown();
                    });
                }
                countDownLatch.await();
            }
        } catch (InterruptedException e) {
            log.error("消息处理被中断", e);
        } finally {
            lockService.release(MSG_RECEIVE_TASK);
        }
    }
}

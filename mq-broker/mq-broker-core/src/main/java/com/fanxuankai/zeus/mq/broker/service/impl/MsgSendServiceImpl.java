package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.constants.LockResourceConstants;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.mapper.MsgSendMapper;
import com.fanxuankai.zeus.mq.broker.service.LockService;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import com.fanxuankai.zeus.util.AddressUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
public class MsgSendServiceImpl extends ServiceImpl<MsgSendMapper, MsgSend>
        implements MsgSendService {

    private final Consumer<MsgSend> successConsumer = msg -> {
        msg.setStatus(Status.SUCCESS.getCode());
        msg.setLastModifiedDate(LocalDateTime.now());
    };
    @Resource
    private LockService lockService;
    @Resource
    private MqBrokerProperties mqBrokerProperties;
    private final BiConsumer<MsgSend, String> unAckConsumer = (msg, cause) -> {
        if (msg.getRetry() + 1 <= mqBrokerProperties.getMaxRetry()) {
            msg.setStatus(Status.CREATED.getCode());
        } else {
            msg.setStatus(Status.FAILURE.getCode());
        }
        msg.setRetry(msg.getRetry() + 1);
        msg.setCause(cause);
        msg.setLastModifiedDate(LocalDateTime.now());
    };

    @Override
    public List<MsgSend> pullData() {
        int runningCount = count(new QueryWrapper<MsgSend>().lambda()
                .eq(MsgSend::getStatus, Status.RUNNING.getCode()));
        if (runningCount > 0) {
            return Collections.emptyList();
        }
        List<MsgSend> records = page(new Page<>(1, mqBrokerProperties.getMsgSendPullDataCount()),
                new QueryWrapper<MsgSend>().lambda()
                        .eq(MsgSend::getStatus, Status.CREATED.getCode())
                        .orderByAsc(MsgSend::getId)
                        .lt(MsgSend::getRetry, mqBrokerProperties.getMaxRetry())).getRecords();
        if (records.isEmpty()) {
            return records;
        }
        MsgSend entity = new MsgSend();
        entity.setStatus(Status.RUNNING.getCode());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setHostAddress(AddressUtils.getHostAddress());
        update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .in(MsgSend::getId, records.stream().map(MsgSend::getId).collect(Collectors.toList())));
        return records;
    }

    @Override
    public void publisherCallbackTimeout() {
        if (!lockService.lock(LockResourceConstants.PUBLISHER_CALLBACK_TIMEOUT)) {
            return;
        }
        try {
            List<MsgSend> list = list(new QueryWrapper<MsgSend>()
                    .lambda()
                    .eq(MsgSend::getStatus, Status.RUNNING.getCode())
                    .lt(MsgSend::getLastModifiedDate,
                            LocalDateTime.now().plus(-mqBrokerProperties.getPublisherCallbackTimeout(),
                                    ChronoUnit.MILLIS)));
            if (list.isEmpty()) {
                return;
            }
            list.forEach(o -> unAckConsumer.accept(o, "回调超时"));
            updateBatchById(list);
        } finally {
            lockService.release(LockResourceConstants.PUBLISHER_CALLBACK_TIMEOUT);
        }
    }

    @Override
    public void success(MsgSend msg) {
        successConsumer.accept(msg);
        updateById(msg);
    }

    @Override
    public void success(String topic, String code) {
        MsgSend msg = new MsgSend();
        successConsumer.accept(msg);
        update(msg, new UpdateWrapper<MsgSend>()
                .lambda()
                .eq(MsgSend::getTopic, topic)
                .eq(MsgSend::getCode, code));
    }

    @Override
    public void failure(String topic, String code, String cause) {
        MsgSend msg = getOne(new QueryWrapper<MsgSend>()
                .lambda()
                .eq(MsgSend::getTopic, topic)
                .eq(MsgSend::getCode,
                        code));
        if (msg == null) {
            return;
        }
        failure(msg, cause);
    }

    @Override
    public void failure(MsgSend msg, String cause) {
        unAckConsumer.accept(msg, cause);
        updateById(msg);
    }

}

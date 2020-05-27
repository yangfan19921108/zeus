package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.Msg;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgSend;
import com.fanxuankai.zeus.mq.broker.mapper.MsgSendMapper;
import com.fanxuankai.zeus.mq.broker.service.MsgSendService;
import com.fanxuankai.zeus.util.AddressUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author fanxuankai
 */
@Component
public class MsgSendServiceImpl extends ServiceImpl<MsgSendMapper, MsgSend>
        implements MsgSendService {

    @Resource
    private MqBrokerProperties mqBrokerProperties;

    @Override
    public List<MsgSend> pullData() {
        return page(new Page<>(1, mqBrokerProperties.getMsgSize()),
                new QueryWrapper<MsgSend>().lambda()
                        .eq(Msg::getStatus, Status.CREATED.getCode())
                        .orderByAsc(Msg::getId)
                        .lt(Msg::getRetry, mqBrokerProperties.getMaxRetry())).getRecords();
    }

    @Override
    public boolean lock(Long id) {
        MsgSend entity = new MsgSend();
        entity.setStatus(Status.RUNNING.getCode());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setHostAddress(AddressUtils.getHostAddress());
        return update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .eq(Msg::getStatus, Status.CREATED.getCode())
                .eq(Msg::getId, id));
    }

    @Override
    public void publisherCallbackTimeout() {
        String increaseRetry = "retry = retry + 1";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeout = now.plus(-mqBrokerProperties.getPublisherCallbackTimeout(), ChronoUnit.MILLIS);
        String cause = "cause = '回调超时'";
        MsgSend entity = new MsgSend();
        entity.setCause(cause);
        entity.setLastModifiedDate(now);
        entity.setStatus(Status.CREATED.getCode());
        update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .setSql(increaseRetry)
                .lt(Msg::getRetry, mqBrokerProperties.getMaxRetry() - 1)
                .eq(Msg::getStatus, Status.RUNNING.getCode())
                .lt(Msg::getLastModifiedDate, timeout));
        entity.setStatus(Status.FAILURE.getCode());
        update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .setSql(increaseRetry)
                .eq(Msg::getRetry, mqBrokerProperties.getMaxRetry() - 1)
                .eq(Msg::getStatus, Status.RUNNING.getCode())
                .lt(Msg::getLastModifiedDate, timeout));

    }

    @Override
    public void success(MsgSend msg) {
        MsgSend entity = new MsgSend();
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setStatus(Status.SUCCESS.getCode());
        update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .eq(Msg::getId, msg.getId())
                .eq(Msg::getStatus, Status.RUNNING.getCode()));
    }

    @Override
    public void success(String topic, String code) {
        MsgSend entity = new MsgSend();
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setStatus(Status.SUCCESS.getCode());
        update(entity, new UpdateWrapper<MsgSend>()
                .lambda()
                .eq(Msg::getTopic, topic)
                .eq(Msg::getCode, code)
                .eq(Msg::getStatus, Status.RUNNING.getCode()));
    }

    @Override
    public void failure(String topic, String code, String cause) {
        MsgSend msg = getOne(new QueryWrapper<MsgSend>()
                .lambda()
                .eq(Msg::getTopic, topic)
                .eq(Msg::getCode, code));
        if (msg == null) {
            return;
        }
        failure(msg, cause);
    }

    @Override
    public void failure(MsgSend msg, String cause) {
        String increaseRetry = "retry = retry + 1";
        MsgSend entity = new MsgSend();
        entity.setCause(cause);
        entity.setLastModifiedDate(LocalDateTime.now());
        if (msg.getRetry() + 1 <= mqBrokerProperties.getMaxRetry()) {
            entity.setStatus(Status.CREATED.getCode());
            update(entity, new UpdateWrapper<MsgSend>()
                    .lambda()
                    .setSql(increaseRetry)
                    .eq(Msg::getStatus, Status.RUNNING.getCode())
            );
        } else {
            entity.setStatus(Status.FAILURE.getCode());
            update(new UpdateWrapper<MsgSend>()
                    .lambda()
                    .setSql(increaseRetry)
                    .eq(Msg::getStatus, Status.RUNNING.getCode())
            );
        }
    }

}

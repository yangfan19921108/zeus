package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.Msg;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MsgReceiveMapper;
import com.fanxuankai.zeus.mq.broker.service.MsgReceiveService;
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
public class MsgReceiveServiceImpl extends ServiceImpl<MsgReceiveMapper, MsgReceive>
        implements MsgReceiveService {

    @Resource
    private MqBrokerProperties mqBrokerProperties;

    @Override
    public List<MsgReceive> pullData() {
        return page(new Page<>(1, mqBrokerProperties.getMsgSize()),
                new QueryWrapper<MsgReceive>()
                        .lambda()
                        .eq(MsgReceive::getStatus, Status.CREATED.getCode())
                        .orderByAsc(MsgReceive::getId)
                        .lt(MsgReceive::getRetry, mqBrokerProperties.getMaxRetry())).getRecords();
    }

    @Override
    public boolean lock(Long id) {
        MsgReceive entity = new MsgReceive();
        entity.setStatus(Status.RUNNING.getCode());
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setHostAddress(AddressUtils.getHostAddress());
        return update(entity, new UpdateWrapper<MsgReceive>()
                .lambda()
                .eq(Msg::getStatus, Status.CREATED.getCode())
                .eq(Msg::getId, id));
    }

    @Override
    public void consumeTimeout() {
        String increaseRetry = "retry = retry + 1";
        LocalDateTime timeout = LocalDateTime.now().plus(-mqBrokerProperties.getConsumeTimeout(),
                ChronoUnit.MILLIS);
        String cause = "cause = '消费超时'";
        MsgReceive entity = new MsgReceive();
        entity.setCause(cause);
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setStatus(Status.CREATED.getCode());
        update(new UpdateWrapper<MsgReceive>()
                .lambda()
                .setSql(increaseRetry)
                .lt(Msg::getRetry, mqBrokerProperties.getMaxRetry() - 1)
                .eq(Msg::getStatus, Status.RUNNING.getCode())
                .lt(Msg::getLastModifiedDate, timeout));
        entity.setStatus(Status.FAILURE.getCode());
        update(new UpdateWrapper<MsgReceive>()
                .lambda()
                .setSql(increaseRetry)
                .eq(Msg::getRetry, mqBrokerProperties.getMaxRetry() - 1)
                .eq(Msg::getStatus, Status.RUNNING.getCode())
                .lt(Msg::getLastModifiedDate, timeout));
    }

    @Override
    public void consumed(MsgReceive msg) {
        MsgReceive entity = new MsgReceive();
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setStatus(Status.SUCCESS.getCode());
        update(entity, new UpdateWrapper<MsgReceive>()
                .lambda()
                .eq(Msg::getId, msg.getId())
                .eq(Msg::getStatus, Status.RUNNING.getCode()));
    }

    @Override
    public void unconsumed(MsgReceive msg, String cause) {
        String increaseRetry = "retry = retry + 1";
        MsgReceive entity = new MsgReceive();
        if (msg.getRetry() + 1 <= mqBrokerProperties.getMaxRetry()) {
            entity.setStatus(Status.CREATED.getCode());
        } else {
            entity.setStatus(Status.FAILURE.getCode());
        }
        entity.setCause(cause);
        entity.setLastModifiedDate(LocalDateTime.now());
        update(entity, new UpdateWrapper<MsgReceive>()
                .lambda()
                .setSql(increaseRetry)
                .eq(Msg::getStatus, Status.RUNNING.getCode())
        );
    }

}

package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.config.MqBrokerProperties;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MsgReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MsgReceiveMapper;
import com.fanxuankai.zeus.mq.broker.service.MsgReceiveService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
        return page(new Page<>(1, mqBrokerProperties.getMsgReceivePullDataCount()),
                new QueryWrapper<MsgReceive>()
                        .lambda()
                        .eq(MsgReceive::getStatus, Status.CREATED.getCode())
                        .orderByAsc(MsgReceive::getId)
                        .lt(MsgReceive::getRetry, mqBrokerProperties.getMaxRetry())).getRecords();
    }

    @Override
    public void consumed(MsgReceive msg) {
        msg.setStatus(Status.SUCCESS.getCode());
        msg.setLastModifiedDate(LocalDateTime.now());
        updateById(msg);
    }

    @Override
    public void unconsumed(MsgReceive msg, String cause) {
        if (msg.getRetry() + 1 <= mqBrokerProperties.getMaxRetry()) {
            msg.setStatus(Status.CREATED.getCode());
        } else {
            msg.setStatus(Status.FAILURE.getCode());
        }
        msg.setRetry(msg.getRetry() + 1);
        msg.setCause(cause);
        msg.setLastModifiedDate(LocalDateTime.now());
        updateById(msg);
    }

}

package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MqBrokerMessage;
import com.fanxuankai.zeus.mq.broker.enums.MessageType;
import com.fanxuankai.zeus.mq.broker.mapper.MqBrokerMessageMapper;
import com.fanxuankai.zeus.mq.broker.service.MqBrokerMessageService;
import com.fanxuankai.zeus.util.AddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Component
@Slf4j
public class MqBrokerMessageServiceImpl extends ServiceImpl<MqBrokerMessageMapper, MqBrokerMessage>
        implements MqBrokerMessageService {

    @Resource
    private MqBrokerMessageMapper mqBrokerMessageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MqBrokerMessage> getAndLock(String queue, long count, int maxRetry, MessageType messageType) {
        MqBrokerMessageMapper mapper = mqBrokerMessageMapper;
        Integer runningCount =
                mapper.selectCount(new QueryWrapper<MqBrokerMessage>().lambda()
                        .eq(MqBrokerMessage::getType, messageType.getCode())
                        .eq(MqBrokerMessage::getStatus, Status.RUNNING.getCode()));
        if (runningCount > 0) {
            throw new LockFailureException();
        }
        LambdaQueryWrapper<MqBrokerMessage> wrapper = new QueryWrapper<MqBrokerMessage>()
                .lambda()
                .eq(MqBrokerMessage::getType, messageType.getCode())
                .eq(MqBrokerMessage::getQueue, queue)
                .eq(MqBrokerMessage::getStatus, Status.CREATED.getCode())
                .orderByAsc(MqBrokerMessage::getId)
                .lt(MqBrokerMessage::getRetry, maxRetry);
        List<MqBrokerMessage> records =
                mapper.selectPage(new Page<>(1, count), wrapper).getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<Long> ids = records.stream().map(MqBrokerMessage::getId).collect(Collectors.toList());
        MqBrokerMessage entity = new MqBrokerMessage();
        Consumer<MqBrokerMessage> lockConsumer = messageSend ->
                messageSend.setStatus(Status.RUNNING.getCode())
                        .setLastModifiedDate(LocalDateTime.now())
                        .setHostAddress(AddressUtils.getHostAddress());
        lockConsumer.accept(entity);
        int lockCount = mapper.update(entity, new UpdateWrapper<MqBrokerMessage>()
                .lambda()
                .eq(MqBrokerMessage::getStatus, Status.CREATED.getCode())
                .lt(MqBrokerMessage::getRetry, maxRetry)
                .in(MqBrokerMessage::getId, ids));
        if (lockCount != records.size()) {
            throw new LockFailureException();
        }
        records.forEach(lockConsumer);
        return records;
    }

    @Override
    public void setSuccess(MqBrokerMessage message) {
        message.setError("");
        message.setHostAddress("");
        mqBrokerMessageMapper.updateById(message.setStatus(Status.SUCCESS.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

    @Override
    public void setFailure(MqBrokerMessage message) {
        mqBrokerMessageMapper.updateById(message.setStatus(Status.FAILURE.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

}

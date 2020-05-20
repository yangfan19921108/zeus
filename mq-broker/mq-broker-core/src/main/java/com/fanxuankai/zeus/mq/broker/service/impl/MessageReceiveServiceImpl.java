package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageReceive;
import com.fanxuankai.zeus.mq.broker.mapper.MessageReceiveMapper;
import com.fanxuankai.zeus.mq.broker.service.MessageReceiveService;
import com.fanxuankai.zeus.util.AddressUtils;
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
public class MessageReceiveServiceImpl implements MessageReceiveService {
    @Resource
    private MessageReceiveMapper messageReceiveMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageReceive> getAndLock(String queue, long count, int maxRetry) {
        MessageReceiveMapper mapper = messageReceiveMapper;
        Integer runningCount =
                mapper.selectCount(new QueryWrapper<MessageReceive>().lambda().eq(MessageReceive::getStatus,
                        Status.RUNNING.getCode()));
        if (runningCount > 0) {
            throw new LockFailureException();
        }
        LambdaQueryWrapper<MessageReceive> wrapper = new QueryWrapper<MessageReceive>()
                .lambda()
                .eq(MessageReceive::getQueue, queue)
                .eq(MessageReceive::getStatus, Status.CREATED.getCode())
                .orderByAsc(MessageReceive::getId)
                .lt(MessageReceive::getRetry, maxRetry);
        List<MessageReceive> records =
                mapper.selectPage(new Page<>(1, count), wrapper).getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<Long> ids = records.stream().map(MessageReceive::getId).collect(Collectors.toList());
        MessageReceive entity = new MessageReceive();
        Consumer<MessageReceive> lockConsumer = messageSend -> messageSend
                .setStatus(Status.RUNNING.getCode())
                .setLastModifiedDate(LocalDateTime.now())
                .setHostAddress(AddressUtils.getHostAddress());
        lockConsumer.accept(entity);
        int lockCount = mapper.update(entity, new UpdateWrapper<MessageReceive>()
                .lambda()
                .eq(MessageReceive::getStatus, Status.CREATED.getCode())
                .lt(MessageReceive::getRetry, maxRetry)
                .in(MessageReceive::getId, ids));
        if (lockCount != records.size()) {
            throw new LockFailureException();
        }
        records.forEach(lockConsumer);
        return records;
    }

    @Override
    public void setSuccess(MessageReceive messageReceive) {
        messageReceive.setError("");
        messageReceive.setHostAddress("");
        messageReceiveMapper.updateById(messageReceive.setStatus(Status.SUCCESS.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

    @Override
    public void setFailure(MessageReceive messageReceive) {
        messageReceiveMapper.updateById(messageReceive.setStatus(Status.FAILURE.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

}

package com.fanxuankai.zeus.mq.broker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.mq.broker.core.LockFailureException;
import com.fanxuankai.zeus.mq.broker.core.Status;
import com.fanxuankai.zeus.mq.broker.domain.MessageSend;
import com.fanxuankai.zeus.mq.broker.mapper.MessageSendMapper;
import com.fanxuankai.zeus.mq.broker.service.MessageSendService;
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
public class MessageSendServiceImpl extends ServiceImpl<MessageSendMapper, MessageSend>
        implements MessageSendService {

    @Resource
    private MessageSendMapper messageSendMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageSend> getAndLock(String queue, long count, int maxRetry) {
        MessageSendMapper mapper = messageSendMapper;
        Integer runningCount =
                mapper.selectCount(new QueryWrapper<MessageSend>().lambda().eq(MessageSend::getStatus,
                        Status.RUNNING.getCode()));
        if (runningCount > 0) {
            throw new LockFailureException();
        }
        LambdaQueryWrapper<MessageSend> wrapper = new QueryWrapper<MessageSend>()
                .lambda()
                .eq(MessageSend::getQueue, queue)
                .eq(MessageSend::getStatus, Status.CREATED.getCode())
                .orderByAsc(MessageSend::getId)
                .lt(MessageSend::getRetry, maxRetry);
        List<MessageSend> records =
                mapper.selectPage(new Page<>(1, count), wrapper).getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<Long> ids = records.stream().map(MessageSend::getId).collect(Collectors.toList());
        MessageSend entity = new MessageSend();
        Consumer<MessageSend> lockConsumer = messageSend ->
                messageSend.setStatus(Status.RUNNING.getCode())
                        .setLastModifiedDate(LocalDateTime.now())
                        .setHostAddress(AddressUtils.getHostAddress());
        lockConsumer.accept(entity);
        int lockCount = mapper.update(entity, new UpdateWrapper<MessageSend>()
                .lambda()
                .eq(MessageSend::getStatus, Status.CREATED.getCode())
                .lt(MessageSend::getRetry, maxRetry)
                .in(MessageSend::getId, ids));
        if (lockCount != records.size()) {
            throw new LockFailureException();
        }
        records.forEach(lockConsumer);
        return records;
    }

    @Override
    public void setSuccess(MessageSend messageSend) {
        messageSend.setError("");
        messageSend.setHostAddress("");
        messageSendMapper.updateById(messageSend.setStatus(Status.SUCCESS.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

    @Override
    public void setFailure(MessageSend messageSend) {
        messageSendMapper.updateById(messageSend.setStatus(Status.FAILURE.getCode())
                .setLastModifiedDate(LocalDateTime.now()));
    }

}

package com.fanxuankai.zeus.mq.broker.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
@TableName("mq_broker_lock")
public class Lock {
    /**
     * 主键
     */
    private Long id;
    /**
     * 锁资源
     */
    private String resource;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;
}

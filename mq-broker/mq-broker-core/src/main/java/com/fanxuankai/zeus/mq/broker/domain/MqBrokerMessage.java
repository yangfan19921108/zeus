package com.fanxuankai.zeus.mq.broker.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
public class MqBrokerMessage {
    /**
     * 主键
     */
    private Long id;
    /**
     * 消息类型 MessageType
     */
    private Integer type;
    /**
     * 队列名
     */
    private String queue;
    /**
     * 代码
     */
    private String code;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 主机地址
     */
    private String hostAddress;
    /**
     * 重试
     */
    private Integer retry;
    /**
     * 失败原因
     */
    private String error;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;
    /**
     * 修改日期
     */
    private LocalDateTime lastModifiedDate;
}

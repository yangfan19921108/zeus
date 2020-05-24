package com.fanxuankai.zeus.mq.broker.core;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息
 *
 * @author fanxuankai
 */
@Data
public class Msg {
    /**
     * 主键
     */
    private Long id;
    /**
     * 主题
     */
    private String topic;
    /**
     * code
     */
    private String code;
    /**
     * 内容
     */
    private String data;
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
    private String cause;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;
    /**
     * 修改日期
     */
    private LocalDateTime lastModifiedDate;
}

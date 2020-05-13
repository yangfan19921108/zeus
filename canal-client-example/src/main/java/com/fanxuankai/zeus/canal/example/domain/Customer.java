package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author fanxuankai
 */
@Entity
@Table(name = "t_customer")
@Getter
@Setter
@CanalToRedis
@CanalToMq
public class Customer extends IntegerLogicDeleteEntity {
    private String userName;
    private Long companyId;
    private String password;
    private Integer randomCode;
    private String realName;
    private Integer status;
    private Integer type;
    private String phone;
    private Long customerId;
    private String openId;
}

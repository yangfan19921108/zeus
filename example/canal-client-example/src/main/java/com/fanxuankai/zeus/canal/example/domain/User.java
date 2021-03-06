package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author fanxuankai
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_user")
@Data
@CanalToRedis
@CanalToMq(repeatableConsumption = true)
public class User extends IntegerLogicDeleteEntity {
    private String phone;
    private String username;
    private String password;
    private int type;
}
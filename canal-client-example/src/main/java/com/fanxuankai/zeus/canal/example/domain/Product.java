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
@Getter
@Setter
@Table(name = "t_product")
@CanalToRedis
@CanalToMq
public class Product extends IntegerLogicDeleteEntity {
    private String name;
    private Double price;
}

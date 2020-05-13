package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.canal.client.core.annotation.Filter;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.redis.annotation.CanalToRedis;
import com.fanxuankai.zeus.data.jpa.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author fanxuankai
 */
@Table(name = "t_order")
@Entity
@Getter
@Setter
@CanalToRedis
@CanalToMq(filter = @Filter(updatedFields = {"status"}, aviatorExpression = "status == 1"))
public class Order extends BaseEntity {
    private Long userId;
    private Long productId;
    private Double orderPrice;
    /**
     * OrderStatus
     */
    private Integer status;
}

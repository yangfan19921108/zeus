package com.fanxuankai.zeus.canal.example.domain;

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
@Entity
@Table(name = "t_relation")
@Getter
@Setter
@CanalToRedis
@CanalToMq
public class Relation extends BaseEntity {
    private Long aId;
    private Long bId;
    private Integer relationType;
}

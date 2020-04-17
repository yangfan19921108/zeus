package com.fanxuankai.zeus.common.data.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * 支持逻辑删除的实体类, Integer 类型类型
 *
 * @author fanxuankai
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class IntegerLogicDeleteEntity extends BaseEntity {

    /**
     * 逻辑删除
     * 0: 否 1: 是
     */
    private Integer deleted = 0;

}

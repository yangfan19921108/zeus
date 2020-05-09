package com.fanxuankai.zeus.data.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * 支持逻辑删除的实体类, Boolean 类型字段
 *
 * @author fanxuankai
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BooleanLogicDeleteEntity extends BaseEntity {

    /**
     * 逻辑删除
     */
    private Boolean deleted = Boolean.FALSE;

}

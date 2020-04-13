package com.fanxuankai.zeus.common.data.jpa.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author fanxuankai
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ddIdGenerator")
    @GenericGenerator(name = "ddIdGenerator", strategy = "com.fanxuankai.zeus.common.data.jpa.id.DdIdGenerator")
    private Long id;

    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private Date createDate;

    @LastModifiedBy
    private Long lastModifiedBy;

    @LastModifiedDate
    private Date lastModifiedDate;

    /**
     * 逻辑删除
     */
    private boolean deleted;

    /**
     * 版本号
     */
    private int version;

}

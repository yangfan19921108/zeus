package com.fanxuankai.zeus.common.data.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 实体类 <br/>
 * 日期暂时用 Date, LocalDateTime Aviator 不支持
 *
 * @author fanxuankai
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ddIdGenerator")
    @GenericGenerator(name = "ddIdGenerator", strategy = "com.fanxuankai.zeus.common.data.jpa.id.DdIdGenerator")
    private Long id;

    /**
     * 创建人
     */
    @CreatedBy
    private Long createdBy;

    /**
     * 创建日期
     */
    @CreatedDate
    private LocalDateTime createDate;

    /**
     * 修改人
     */
    @LastModifiedBy
    private Long lastModifiedBy;

    /**
     * 修改时间
     */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    /**
     * 版本号
     */
    private Integer version;
}

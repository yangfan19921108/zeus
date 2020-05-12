package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author fanxuankai
 */
@Entity
@Table(name = "t_company")
@Getter
@Setter
public class Company extends IntegerLogicDeleteEntity {
    private String companyCode;
    private String companyName;
    private Long parentId;
    private Integer companyType;
    private String principal;
    private String cellphone;
    private String address;
    private Integer status;
}

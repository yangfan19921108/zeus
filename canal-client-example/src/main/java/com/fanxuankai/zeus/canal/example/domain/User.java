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
@Table(name = "t_user")
@Getter
@Setter
public class User extends IntegerLogicDeleteEntity {
    private String phone;
    private String username;
    private String password;
    private int type;
}
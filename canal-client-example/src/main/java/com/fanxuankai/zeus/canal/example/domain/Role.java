package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.data.jpa.domain.BooleanLogicDeleteEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author fanxuankai
 */
@Entity
@Table(name = "t_role")
@Getter
@Setter
public class Role extends BooleanLogicDeleteEntity {
    private String name;
}

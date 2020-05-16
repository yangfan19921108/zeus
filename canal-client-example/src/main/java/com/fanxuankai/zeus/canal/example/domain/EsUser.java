package com.fanxuankai.zeus.canal.example.domain;

import com.fanxuankai.zeus.canal.client.core.annotation.CanalTable;
import com.fanxuankai.zeus.canal.client.es.annotation.CanalToEs;
import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author fanxuankai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@CanalTable(name = "t_user")
@CanalToEs
@Document(indexName = "canal_client_example.t_user")
public class EsUser extends IntegerLogicDeleteEntity {
    private String phone;
    private String username;
    private String password;
    private int type;
}
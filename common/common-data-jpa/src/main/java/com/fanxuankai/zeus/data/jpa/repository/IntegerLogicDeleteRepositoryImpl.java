package com.fanxuankai.zeus.data.jpa.repository;

import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 支持逻辑删除的 Repository 实现类, Integer 类型字段
 *
 * @author fanxuankai
 */
public class IntegerLogicDeleteRepositoryImpl<ID extends Serializable>
        extends AbstractLogicDeleteRepository<IntegerLogicDeleteEntity, ID> {

    public IntegerLogicDeleteRepositoryImpl(Class<IntegerLogicDeleteEntity> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public void logicDelete(IntegerLogicDeleteEntity entity) {
        entity.setDeleted(1);
        save(entity);
    }

    @Override
    public void batchLogicDelete(Iterable<IntegerLogicDeleteEntity> entities) {
        entities.forEach(t -> t.setDeleted(1));
        batchUpdate(entities);
    }

}

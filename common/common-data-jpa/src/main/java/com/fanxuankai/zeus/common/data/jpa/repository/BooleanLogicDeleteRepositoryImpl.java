package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.domain.BooleanLogicDeleteEntity;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 支持逻辑删除的 Repository 实现类, Boolean 类型字段
 *
 * @author fanxuankai
 */
public class BooleanLogicDeleteRepositoryImpl<ID extends Serializable>
        extends AbstractLogicDeleteRepository<BooleanLogicDeleteEntity, ID> {

    public BooleanLogicDeleteRepositoryImpl(Class<BooleanLogicDeleteEntity> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public void logicDelete(BooleanLogicDeleteEntity entity) {
        entity.setDeleted(Boolean.TRUE);
        save(entity);
    }

    @Override
    public void batchLogicDelete(Iterable<BooleanLogicDeleteEntity> entities) {
        entities.forEach(t -> t.setDeleted(Boolean.TRUE));
        batchUpdate(entities);
    }
}

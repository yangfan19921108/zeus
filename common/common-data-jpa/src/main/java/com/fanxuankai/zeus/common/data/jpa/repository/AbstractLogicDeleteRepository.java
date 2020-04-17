package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.domain.BaseEntity;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 支持逻辑删除的抽象 Repository
 *
 * @author fanxuankai
 */
public abstract class AbstractLogicDeleteRepository<T extends BaseEntity, ID extends Serializable>
        extends BatchOperationRepositoryImpl<T, ID> implements LogicDeleteRepository<T, ID> {

    public AbstractLogicDeleteRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public void logicDeleteById(ID id) {
        findById(id).ifPresent(this::logicDelete);
    }

    @Override
    public void batchLogicDeleteById(Iterable<ID> ids) {
        batchLogicDelete(findAllById(ids));
    }
}

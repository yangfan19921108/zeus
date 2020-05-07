package com.fanxuankai.zeus.data.jpa.repository;

import com.fanxuankai.zeus.data.jpa.domain.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 支持逻辑删除的 Repository
 *
 * @author fanxuankai
 */
@NoRepositoryBean
public interface LogicDeleteRepository<T extends BaseEntity, ID extends Serializable>
        extends BatchOperationRepository<T, ID> {

    /**
     * 逻辑删除
     *
     * @param entity 删除前
     */
    void logicDelete(T entity);

    /**
     * 批量逻辑删除
     *
     * @param entities 删除前
     */
    void batchLogicDelete(Iterable<T> entities);

    /**
     * 逻辑删除
     *
     * @param id 主键
     */
    void logicDeleteById(ID id);

    /**
     * 批量逻辑删除
     *
     * @param ids id
     */
    void batchLogicDeleteById(Iterable<ID> ids);
}

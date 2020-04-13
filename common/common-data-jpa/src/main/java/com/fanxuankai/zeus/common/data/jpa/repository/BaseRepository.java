package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanxuankai
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable>
        extends JpaRepository<T, ID> {
    /**
     * 批量保存
     *
     * @param entities 保存前
     * @return 保存后
     */
    List<T> batchSave(Iterable<T> entities);

    /**
     * 批量更新
     *
     * @param entities 更新前
     * @return 更新后
     */
    List<T> batchUpdate(Iterable<T> entities);

    /**
     * 批量删除
     *
     * @param ids 主键
     */
    void batchDelete(Iterable<ID> ids);

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

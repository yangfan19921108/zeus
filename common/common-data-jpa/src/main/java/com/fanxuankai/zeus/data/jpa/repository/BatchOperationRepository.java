package com.fanxuankai.zeus.data.jpa.repository;

import com.fanxuankai.zeus.data.jpa.domain.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * 支持批量操作的 Repository 接口
 *
 * @author fanxuankai
 */
@NoRepositoryBean
public interface BatchOperationRepository<T extends BaseEntity, ID extends Serializable>
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
}

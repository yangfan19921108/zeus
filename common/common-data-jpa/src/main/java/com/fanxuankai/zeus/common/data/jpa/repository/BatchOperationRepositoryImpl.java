package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.domain.BaseEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 支持批量操作的 Repository 接口
 *
 * @author fanxuankai
 */
public class BatchOperationRepositoryImpl<T extends BaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BatchOperationRepository<T, ID> {

    private static final int DEFAULT_BATCH_SIZE = 5000;
    protected final EntityManager em;

    public BatchOperationRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> batchSave(Iterable<T> entities) {
        Iterator<T> iterator = entities.iterator();
        List<T> result = new ArrayList<>();
        int index = 0;
        while (iterator.hasNext()) {
            T entity = iterator.next();
            em.persist(entity);
            result.add(entity);
            index++;
            if (index % DEFAULT_BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
        if (index % DEFAULT_BATCH_SIZE != 0) {
            em.flush();
            em.clear();
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> batchUpdate(Iterable<T> entities) {
        Iterator<T> iterator = entities.iterator();
        List<T> result = new ArrayList<>();
        int index = 0;
        while (iterator.hasNext()) {
            result.add(em.merge(iterator.next()));
            index++;
            if (index % DEFAULT_BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
        if (index % DEFAULT_BATCH_SIZE != 0) {
            em.flush();
            em.clear();
        }
        return result;
    }

}

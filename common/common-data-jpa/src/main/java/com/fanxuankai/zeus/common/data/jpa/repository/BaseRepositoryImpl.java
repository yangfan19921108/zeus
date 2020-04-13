package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author fanxuankai
 */
public class BaseRepositoryImpl<T extends BaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private static final int DEFAULT_BATCH_SIZE = 5000;
    private final EntityManager em;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
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

    @Override
    public void batchDelete(Iterable<ID> ids) {
        deleteInBatch(findAllById(ids));
    }

    @Override
    public void logicDelete(T entity) {
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public void batchLogicDelete(Iterable<T> entities) {
        entities.forEach(t -> t.setDeleted(true));
        batchSave(entities);
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

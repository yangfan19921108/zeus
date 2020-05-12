package com.fanxuankai.zeus.data.jpa.config;

import com.fanxuankai.zeus.data.jpa.domain.BaseEntity;
import com.fanxuankai.zeus.data.jpa.domain.BooleanLogicDeleteEntity;
import com.fanxuankai.zeus.data.jpa.domain.IntegerLogicDeleteEntity;
import com.fanxuankai.zeus.data.jpa.repository.BatchOperationRepositoryImpl;
import com.fanxuankai.zeus.data.jpa.repository.BooleanLogicDeleteRepositoryImpl;
import com.fanxuankai.zeus.data.jpa.repository.IntegerLogicDeleteRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * BeanRepository 工厂
 *
 * @author fanxuankai
 */
public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, I> {

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    @Nonnull
    protected RepositoryFactorySupport createRepositoryFactory(@Nonnull EntityManager em) {
        return new MyRepositoryFactory<>(em);
    }

    private static class MyRepositoryFactory<T extends BaseEntity> extends JpaRepositoryFactory {

        public MyRepositoryFactory(EntityManager em) {
            super(em);
        }

        @Override
        @SuppressWarnings("rawtypes unchecked")
        @Nonnull
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                        @Nonnull EntityManager entityManager) {
            Class<T> domainClass = (Class<T>) information.getDomainType();
            if (BooleanLogicDeleteEntity.class.isAssignableFrom(domainClass)) {
                return new BooleanLogicDeleteRepositoryImpl(domainClass, entityManager);
            }
            if (IntegerLogicDeleteEntity.class.isAssignableFrom(domainClass)) {
                return new IntegerLogicDeleteRepositoryImpl(domainClass, entityManager);
            }
            if (BaseEntity.class.isAssignableFrom(domainClass)) {
                return new BatchOperationRepositoryImpl<>(domainClass, entityManager);
            } else {
                return new SimpleJpaRepository<>(domainClass, entityManager);
            }
        }

        @Override
        @Nonnull
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            Class<?> domainClass = metadata.getDomainType();
            if (BooleanLogicDeleteEntity.class.isAssignableFrom(domainClass)) {
                return BooleanLogicDeleteRepositoryImpl.class;
            }
            if (IntegerLogicDeleteEntity.class.isAssignableFrom(domainClass)) {
                return IntegerLogicDeleteRepositoryImpl.class;
            }
            if (BaseEntity.class.isAssignableFrom(domainClass)) {
                return BatchOperationRepositoryImpl.class;
            } else {
                return SimpleJpaRepository.class;
            }
        }
    }

}

package com.fanxuankai.zeus.common.data.jpa.repository;

import com.fanxuankai.zeus.common.data.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
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
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new MyRepositoryFactory(em);
    }

    private static class MyRepositoryFactory<T extends BaseEntity, I extends Serializable> extends JpaRepositoryFactory {

        public MyRepositoryFactory(EntityManager em) {
            super(em);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                        EntityManager entityManager) {
            Class<T> domainClass = (Class<T>) information.getDomainType();
            if (BaseEntity.class.isAssignableFrom(domainClass)) {
                return new BaseRepositoryImpl<>(domainClass, entityManager);
            } else {
                return new SimpleJpaRepository<>(domainClass, entityManager);
            }

        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseEntity.class.isAssignableFrom(metadata.getDomainType()) ? BaseRepositoryImpl.class :
                    SimpleJpaRepository.class;
        }
    }
}

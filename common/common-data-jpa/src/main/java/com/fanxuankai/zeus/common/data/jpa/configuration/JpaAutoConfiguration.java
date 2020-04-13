package com.fanxuankai.zeus.common.data.jpa.configuration;

import com.fanxuankai.zeus.common.data.jpa.repository.BaseRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author fanxuankai
 */
@EnableJpaAuditing
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class JpaAutoConfiguration {
}

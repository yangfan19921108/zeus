package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.client.core.annotation.EnableCanal;
import com.fanxuankai.zeus.data.jpa.config.BaseRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author fanxuankai
 */
@SpringBootApplication
@EnableCanal(schema = "canal_client_example")
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class CanalClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalClientExampleApplication.class, args);
    }

}

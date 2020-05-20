package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.client.core.annotation.EnableCanal;
import com.fanxuankai.zeus.data.jpa.config.BaseRepositoryFactoryBean;
import com.fanxuankai.zeus.mq.broker.config.EnableMqBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author fanxuankai
 */
@SpringBootApplication
@EnableCanal(schema = "canal_client_example")
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableMqBroker
public class CanalClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalClientExampleApplication.class, args);
    }

}

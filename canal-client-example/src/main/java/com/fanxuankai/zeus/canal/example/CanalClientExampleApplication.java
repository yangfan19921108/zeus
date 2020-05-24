package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.client.core.annotation.EnableCanal;
import com.fanxuankai.zeus.data.jpa.config.BaseRepositoryFactoryBean;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.produce.EventPublisher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fanxuankai
 */
@SpringBootApplication
@EnableCanal(schema = "canal_client_example")
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class CanalClientExampleApplication implements ApplicationRunner {

    @Resource
    private EventPublisher eventPublisher;

    public static void main(String[] args) {
        SpringApplication.run(CanalClientExampleApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        IntStream.range(0, 3)
                .mapToObj(value -> "user" + value)
                .forEach(s -> eventPublisher.publish(IntStream.range(0, 100)
                        .mapToObj(value -> new Event()
                                .setName(s)
                                .setKey(UUID.randomUUID().toString())
                                .setData("fanxuankai"))
                        .collect(Collectors.toList()), true));
    }
}

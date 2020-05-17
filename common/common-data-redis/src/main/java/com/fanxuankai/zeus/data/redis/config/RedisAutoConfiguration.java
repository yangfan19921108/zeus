package com.fanxuankai.zeus.data.redis.config;

import com.fanxuankai.zeus.data.redis.ObjectRedisTemplate;
import com.fanxuankai.zeus.data.redis.lock.DistributedLocker;
import com.fanxuankai.zeus.data.redis.lock.RedisLocker;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public ObjectRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new ObjectRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        List<String> nodes = Optional.ofNullable(redisProperties.getCluster())
                .map(RedisProperties.Cluster::getNodes)
                .orElse(Collections.emptyList());
        Config config = new Config();
        if (!nodes.isEmpty()) {
            //这是用的集群server
            config.useClusterServers()
                    //设置集群状态扫描时间
                    .setScanInterval(2000)
                    .addNodeAddress(nodes.stream().map(s -> "redis://" + s)
                            .collect(Collectors.toList()).toArray(new String[]{}))
                    .setPassword(redisProperties.getPassword());
        } else {
            config.useSingleServer()
                    .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLocker distributedLocker() {
        return new RedisLocker();
    }
}

package com.fanxuankai.zeus.data.redis.config;

import com.fanxuankai.zeus.data.redis.lock.DistributedLocker;
import com.fanxuankai.zeus.data.redis.lock.RedisLocker;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> string = RedisSerializer.string();
        template.setKeySerializer(string);
        template.setHashKeySerializer(string);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<?> json = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        json.setObjectMapper(mapper);

        template.setValueSerializer(json);
        template.setHashValueSerializer(json);

        template.afterPropertiesSet();

        return template;
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

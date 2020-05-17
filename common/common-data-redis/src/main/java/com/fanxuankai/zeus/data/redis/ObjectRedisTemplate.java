package com.fanxuankai.zeus.data.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @author fanxuankai
 */
public class ObjectRedisTemplate extends RedisTemplate<Object, Object> {

    public ObjectRedisTemplate() {
        Jackson2JsonRedisSerializer<?> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        this.setKeySerializer(jsonSerializer);
        this.setValueSerializer(jsonSerializer);
        this.setHashKeySerializer(jsonSerializer);
        this.setHashValueSerializer(jsonSerializer);
    }

    public ObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

}

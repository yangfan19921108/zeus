package com.fanxuankai.zeus.canal.client.redis.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author fanxuankai
 */
@Configuration
@Import({RedisRepositoryAutoConfigurationImportRegistrar.class})
public class RedisRepositoryAutoConfiguration {

}

package com.fanxuankai.zeus.canal.client.redis.config;

import org.springframework.context.annotation.Import;

/**
 * Redis 自动装配<p>
 * 说下为什么要用到 Registrar, 而不是 Selector ?<p>
 * Redis 自动装配依赖于 @DefaultSchema, 而其 import 采用 Registrar 方式;<p>
 * 如果使用 Selector, 会提前于 @DefaultSchema, 而又依赖于它;<p>
 * 此时依赖性并未加载, 必然会导致诸多严重性的问题产生。
 *
 * @author fanxuankai
 */
@Import({RedisAutoConfigurationImportRegistrar.class})
public class RedisAutoConfiguration {

}

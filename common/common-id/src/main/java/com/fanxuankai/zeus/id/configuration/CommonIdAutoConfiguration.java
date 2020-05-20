package com.fanxuankai.zeus.id.configuration;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.fanxuankai.zeus.id.DangDangIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author fanxuankai
 */
public class CommonIdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdGenerator idGenerator() {
        return DangDangIdGenerator.getInstance();
    }
}

package com.fanxuankai.zeus.util.config;

import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fanxuankai
 */
public class CommonUtilAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public ThreadPoolExecutor threadPoolExecutor() {
        return ThreadPoolService.getInstance();
    }

}

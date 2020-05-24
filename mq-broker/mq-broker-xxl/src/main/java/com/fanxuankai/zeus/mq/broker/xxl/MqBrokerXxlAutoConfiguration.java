package com.fanxuankai.zeus.mq.broker.xxl;

import com.fanxuankai.zeus.mq.broker.core.produce.MqProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author fanxuankai
 */
@Import({MqBrokerXxlAutoConfigurationImportRegistrar.class})
public class MqBrokerXxlAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MqProducer.class)
    public XxlMqProducer mqProducer() {
        return new XxlMqProducer();
    }
}

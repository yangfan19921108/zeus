package com.fanxuankai.zeus.canal.client.rabbit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author fanxuankai
 */
@Import({RabbitListenerAutoConfigurationImportRegistrar.class})
@Configuration
public class RabbitListenerAutoConfiguration {

}

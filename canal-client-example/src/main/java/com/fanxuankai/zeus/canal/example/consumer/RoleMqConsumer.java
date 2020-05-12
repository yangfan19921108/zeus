package com.fanxuankai.zeus.canal.example.consumer;

import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.example.domain.Role;
import org.springframework.stereotype.Service;

/**
 * @author fanxuankai
 */
@Service
public class RoleMqConsumer implements MqConsumer<Role> {

}

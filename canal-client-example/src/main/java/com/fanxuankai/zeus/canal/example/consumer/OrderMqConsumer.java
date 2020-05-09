package com.fanxuankai.zeus.canal.example.consumer;

import com.fanxuankai.zeus.canal.client.core.annotation.Filter;
import com.fanxuankai.zeus.canal.client.mq.core.annotation.CanalToMq;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.example.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
@CanalToMq(filter = @Filter(updatedFields = {"status"}, aviatorExpression = "status == 1"))
public class OrderMqConsumer implements MqConsumer<Order> {

    @Override
    public void update(Order before, Order after) {
        log.info("订单完成: {} {}", after.getId(), after.getStatus());
    }
}

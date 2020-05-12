package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Order;
import com.fanxuankai.zeus.canal.example.util.MockUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderRepositoryTest {
    @Resource
    private OrderRepository orderRepository;

    @Test
    public void saveAll() {
        orderRepository.batchSave(MockUtils.mock(0, 100000, Order.class));
    }
}

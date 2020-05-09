package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Product;
import com.fanxuankai.zeus.canal.example.util.MockUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductRepositoryTest {
    @Resource
    private ProductRepository productRepository;

    @Test
    public void init() {
        List<Product> list = MockUtils.mock(1, 100000, Product.class);
        list.forEach(o -> o.setId(null));
        productRepository.batchSave(list);
    }
}

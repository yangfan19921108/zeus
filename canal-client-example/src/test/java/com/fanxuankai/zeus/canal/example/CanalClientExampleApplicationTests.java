package com.fanxuankai.zeus.canal.example;

import com.fanxuankai.zeus.canal.example.domain.*;
import com.fanxuankai.zeus.canal.example.repository.*;
import com.fanxuankai.zeus.util.MockUtils;
import com.fanxuankai.zeus.util.concurrent.ThreadPoolService;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class CanalClientExampleApplicationTests {

    @Resource
    private CompanyRepository companyRepository;
    @Resource
    private CustomerRepository customerRepository;
    @Resource
    private ProductRepository productRepository;
    @Resource
    private RelationRepository relationRepository;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private UserRepository userRepository;

    @Test
    public void contextLoads() throws InterruptedException {
        int startInclusive = 0;
        int endExclusive = 100000;
        CountDownLatch countDownLatch = new CountDownLatch(6);
        Stopwatch sw = Stopwatch.createStarted();
        ThreadPoolService.getInstance().execute(() -> {
            companyRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, Company.class));
            countDownLatch.countDown();
        });
        ThreadPoolService.getInstance().execute(() -> {
            productRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, Product.class));
            countDownLatch.countDown();
        });
        ThreadPoolService.getInstance().execute(() -> {
            customerRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, Customer.class));
            countDownLatch.countDown();
        });
        ThreadPoolService.getInstance().execute(() -> {
            relationRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, Relation.class));
            countDownLatch.countDown();
        });
        ThreadPoolService.getInstance().execute(() -> {
            userRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, User.class));
            countDownLatch.countDown();
        });
        ThreadPoolService.getInstance().execute(() -> {
            roleRepository.batchSave(MockUtils.mock(startInclusive,
                    endExclusive, Role.class));
            countDownLatch.countDown();
        });
        countDownLatch.await();
        sw.stop();
        log.info("Complete Init Data. {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
    }
}

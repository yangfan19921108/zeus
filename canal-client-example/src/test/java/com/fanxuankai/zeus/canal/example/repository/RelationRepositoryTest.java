package com.fanxuankai.zeus.canal.example.repository;

import com.fanxuankai.zeus.canal.example.domain.Relation;
import com.github.jsonzou.jmockdata.JMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RelationRepositoryTest {
    @Resource
    private RelationRepository relationRepository;

    @Test
    public void batchSave() {
        relationRepository.batchSave(IntStream.range(0, 1)
                .mapToObj(o -> JMockData.mock(Relation.class))
                .peek(relation -> relation.setId(null))
                .collect(Collectors.toList()));

    }
}

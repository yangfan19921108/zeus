package com.fanxuankai.zeus.canal.client.redis.consumer;

import com.fanxuankai.zeus.canal.client.core.wrapper.EntryWrapper;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 修改事件消费者
 * 处理逻辑: 删除旧的数据, 插入新的数据
 * 为什么先删除再插入? 因为缓存策略可能已经发生变化, 此时旧数据已经无法满足现有的策略。
 *
 * @author fanxuankai
 */
public class UpdateConsumer extends AbstractRedisConsumer<UpdateConsumer.ProcessData> {

    @Resource
    private InsertConsumer insertConsumer;
    @Resource
    private DeleteConsumer deleteConsumer;

    @Override
    public ProcessData process(EntryWrapper entryWrapper) {
        Map<String, Map<String, Object>> forInsert = insertConsumer.process(entryWrapper);
        Map<String, List<String>> forDelete = deleteConsumer.process(entryWrapper);
        ProcessData processData = new ProcessData();
        processData.setForDelete(forDelete);
        processData.setForInsert(forInsert);
        return processData;
    }

    @Override
    public void consume(ProcessData processData) {
        if (processData.forDelete.isEmpty() || processData.forInsert.isEmpty()) {
            return;
        }
        deleteConsumer.consume(processData.forDelete);
        insertConsumer.consume(processData.forInsert);
    }

    @Getter
    @Setter
    public static class ProcessData {
        private Map<String, List<String>> forDelete;
        private Map<String, Map<String, Object>> forInsert;
    }
}

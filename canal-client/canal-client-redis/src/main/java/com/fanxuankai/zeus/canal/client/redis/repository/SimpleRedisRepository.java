package com.fanxuankai.zeus.canal.client.redis.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fanxuankai.zeus.canal.client.core.metadata.CanalTableMetadata;
import com.fanxuankai.zeus.canal.client.core.util.RedisUtils;
import com.fanxuankai.zeus.canal.client.redis.metadata.CanalToRedisMetadata;
import com.fanxuankai.zeus.canal.client.redis.model.CombineKeyModel;
import com.fanxuankai.zeus.canal.client.redis.model.Entry;
import com.fanxuankai.zeus.canal.client.redis.model.UniqueKey;
import com.fanxuankai.zeus.canal.client.redis.model.UniqueKeyPro;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.fanxuankai.zeus.canal.client.redis.config.RedisRepositoryScanner.INTERFACE_BEAN_SCANNER;

/**
 * RedisRepository 实现类
 *
 * @author fanxuankai
 */
public class SimpleRedisRepository implements RedisRepository<Object> {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    private Class<Object> domainType;
    private CanalTableMetadata canalTableMetadata;
    private CanalToRedisMetadata canalToRedisMetadata;

    /**
     * SimpleRedisRepository 合理的话, 应该使用泛型
     * 由于 Javassist 不支持泛型, 暂且使用设置 domainType 的方式
     *
     * @param domainType 实体类型
     */
    protected void setDomainType(Class<Object> domainType) {
        this.domainType = domainType;
        this.canalTableMetadata = INTERFACE_BEAN_SCANNER.getCanalTableMetadata(domainType);
        this.canalToRedisMetadata = INTERFACE_BEAN_SCANNER.getMetadata(canalTableMetadata.getSchema(),
                canalTableMetadata.getName());
    }

    @Override
    public Optional<Object> findById(Object id) {
        return Optional.ofNullable(convert(redisTemplate.opsForHash().get(key(), id.toString())));
    }

    @Override
    public boolean existsById(Object id) {
        return getOne(id) != null;
    }

    @Override
    public List<Object> findAll() {
        return getAll(key());
    }

    @Override
    public List<Object> findAllById(Iterable<Object> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        Set<Object> idSet = Sets.newHashSet();
        for (Object id : ids) {
            idSet.add(id.toString());
        }
        return multiGet(key(), idSet);
    }

    @Override
    public long count() {
        return redisTemplate.opsForHash().size(key());
    }

    @Override
    public Object getOne(Object id) {
        Optional<Object> optional = findById(id);
        if (optional.isEmpty()) {
            throw new NullPointerException();
        }
        return optional.get();
    }

    @Override
    public Optional<Object> findOne(UniqueKey uniqueKey) {
        return Optional.ofNullable(convert(redisTemplate.opsForHash().get(keyWithSuffix(uniqueKey.getName()),
                uniqueKey.getValue().toString())));
    }

    @Override
    public boolean exists(UniqueKey uniqueKey) {
        return findOne(uniqueKey).isPresent();
    }

    @Override
    public Optional<Object> findOne(CombineKeyModel combineKeyModel) {
        List<Entry> entries = combineKeyModel.getEntries();
        List<String> names = entries.stream().map(Entry::getName).collect(Collectors.toList());
        String suffix = RedisUtils.keySuffix(names);
        String key = keyWithSuffix(suffix);
        String hashKey = RedisUtils.combineHashKey(names, entries.stream().collect(Collectors.toMap(Entry::getName,
                o -> o.getValue().toString())));
        return Optional.ofNullable(convert(redisTemplate.opsForHash().get(key, hashKey)));
    }

    @Override
    public Object getOne(CombineKeyModel combineKeyModel) {
        Optional<Object> optional = findOne(combineKeyModel);
        if (optional.isEmpty()) {
            throw new NullPointerException();
        }
        return optional.get();
    }

    @Override
    public List<Object> findAll(List<String> combineKey) {
        String suffix = RedisUtils.keySuffix(combineKey);
        String key = keyWithSuffix(suffix);
        return getAll(key);
    }

    @Override
    public List<Object> findAll(UniqueKeyPro uniqueKeyPro) {
        String key = keyWithSuffix(uniqueKeyPro.getName());
        Set<Object> hashKeys = new HashSet<>();
        for (Object value : uniqueKeyPro.getValues()) {
            hashKeys.add(value.toString());
        }
        return multiGet(key, hashKeys);
    }

    @Override
    public Object getOne(UniqueKey uniqueKey) {
        Optional<Object> optional = findOne(uniqueKey);
        if (optional.isEmpty()) {
            throw new NullPointerException();
        }
        return optional.get();
    }

    @Override
    public List<Object> findAll(String uniqueKey) {
        return convert(getAll(keyWithSuffix(uniqueKey)));
    }

    private List<Object> getAll(String key) {
        return convert(redisTemplate.opsForHash().values(key));
    }

    private List<Object> multiGet(String key, Collection<Object> hashKeys) {
        return convert(redisTemplate.opsForHash().multiGet(key, hashKeys));
    }

    private List<Object> convert(List<Object> objects) {
        objects = objects.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(objects)) {
            return Collections.emptyList();
        }
        return new JSONArray(objects).toJavaList(domainType);
    }

    private Object convert(Object o) {
        if (o == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(o), domainType);
    }

    private String key() {
        if (!StringUtils.isBlank(canalToRedisMetadata.getKey())) {
            return canalToRedisMetadata.getKey();
        }
        return RedisUtils.key(schema(), tableName());
    }

    private String keyWithSuffix(String suffix) {
        if (!StringUtils.isBlank(canalToRedisMetadata.getKey())) {
            return RedisUtils.customKey(canalToRedisMetadata.getKey(), suffix);
        }
        return RedisUtils.key(schema(), tableName(), suffix);
    }

    private String schema() {
        return canalTableMetadata.getSchema();
    }

    private String tableName() {
        return canalTableMetadata.getName();
    }
}

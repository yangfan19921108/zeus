package com.fanxuankai.zeus.common.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.common.dict.domain.SysDictType;

import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 查字典类型
     *
     * @param name 类型名
     * @return 字典类型
     */
    default SysDictType get(String name) {
        return getOne(new QueryWrapper<SysDictType>().lambda().eq(SysDictType::getName, name));
    }

    /**
     * 新增字典类型
     *
     * @param name        名称
     * @param description 描述
     */
    default void add(String name, String description) {
        save(new SysDictType().setName(name).setDescription(description).setCreateDate(LocalDateTime.now()));
    }

    /**
     * 新增字典类型
     *
     * @param name        名称
     * @param description 描述
     * @return 返回id
     */
    default Long addAndGet(String name, String description) {
        add(name, description);
        return get(name).getId();
    }

    /**
     * 删除字典类型
     *
     * @param name 名称
     */
    default void delete(String name) {
        remove(new QueryWrapper<SysDictType>().lambda().eq(SysDictType::getName, name));
    }
}

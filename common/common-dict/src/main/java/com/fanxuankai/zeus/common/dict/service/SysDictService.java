package com.fanxuankai.zeus.common.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxuankai.zeus.common.dict.domain.SysDict;
import com.fanxuankai.zeus.common.dict.domain.SysDictType;
import com.fanxuankai.zeus.common.dict.vo.SysDictVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
public interface SysDictService extends IService<SysDict> {
    /**
     * 查字典
     *
     * @param typeName 字典类型名
     * @return list
     */
    SysDictVO list(String typeName);

    /**
     * 根据字典类型获取字典
     *
     * @param typeIds 类型id
     * @return key: 类型id value: 该类型的所有字典
     */
    default Map<Long, List<SysDict>> map(List<Long> typeIds) {
        return list(new QueryWrapper<SysDict>().lambda()
                .in(SysDict::getTypeId, typeIds))
                .stream()
                .collect(Collectors.groupingBy(SysDict::getTypeId));
    }

    /**
     * 新增字典
     *
     * @param sysDict 字典
     */
    default void add(SysDict sysDict) {
        if (sysDict.getCreateDate() == null) {
            sysDict.setCreateDate(LocalDateTime.now());
        }
        save(sysDict);
    }

    /**
     * 新增所有字典
     *
     * @param sysDictList 字典
     * @param auto        code、sort 自动赋值
     */
    default void addAll(List<SysDict> sysDictList, boolean auto) {
        if (auto) {
            for (int i = 0; i < sysDictList.size(); i++) {
                SysDict sysDict = sysDictList.get(i);
                sysDict.setCode(i);
                sysDict.setSort(i * 100);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        sysDictList.forEach(sysDict -> sysDict.setCreateDate(now));
        saveBatch(sysDictList);
    }

    /**
     * 新增字典, 自动创建字典类型
     *
     * @param dictType 类型
     * @param sysDict  字典
     */
    void add(SysDictType dictType, SysDict sysDict);

    /**
     * 新增所有字典, 自动创建字典类型
     *
     * @param dictType    类型
     * @param sysDictList 字典
     * @param auto        code、sort 自动赋值
     */
    void addAll(SysDictType dictType, List<SysDict> sysDictList, boolean auto);

    /**
     * 删除字典
     *
     * @param typeName 类型名称
     */
    void delete(String typeName);
}

package com.fanxuankai.zeus.common.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxuankai.zeus.common.dict.domain.SysDict;
import com.fanxuankai.zeus.common.dict.domain.SysDictType;
import com.fanxuankai.zeus.common.dict.mapper.SysDictMapper;
import com.fanxuankai.zeus.common.dict.service.SysDictService;
import com.fanxuankai.zeus.common.dict.service.SysDictTypeService;
import com.fanxuankai.zeus.common.dict.vo.SysDictVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Resource
    private SysDictTypeService sysDictTypeService;

    @Override
    public SysDictVO list(String typeName) {
        SysDictType sysDictType = sysDictTypeService.get(typeName);
        if (sysDictType == null) {
            return null;
        }
        SysDictVO vo = new SysDictVO();
        vo.setTypeName(typeName);
        vo.setTypeDescription(sysDictType.getDescription());
        vo.setDictList(list(new QueryWrapper<SysDict>().lambda().eq(SysDict::getTypeId,
                sysDictType.getId()))
                .stream()
                .sorted(Comparator.comparing(SysDict::getSort))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public void add(SysDictType dictType, SysDict sysDict) {
        sysDict.setTypeId(sysDictTypeService.addAndGet(dictType.getName(), dictType.getDescription()));
        add(sysDict);
    }

    @Override
    public void addAll(SysDictType dictType, List<SysDict> sysDictList, boolean auto) {
        Long typeId = sysDictTypeService.addAndGet(dictType.getName(), dictType.getDescription());
        sysDictList.forEach(sysDict -> sysDict.setTypeId(typeId));
        addAll(sysDictList, auto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String typeName) {
        Long typeId = sysDictTypeService.get(typeName).getId();
        sysDictTypeService.delete(typeName);
        remove(new QueryWrapper<SysDict>().lambda().eq(SysDict::getTypeId, typeId));
    }
}

package com.fanxuankai.zeus.common.dict.vo;

import com.fanxuankai.zeus.common.dict.domain.SysDict;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
public class SysDictVO {

    /**
     * 字典类型名
     */
    private String typeName;
    /**
     * 字典类型描述
     */
    private String typeDescription;

    private List<SysDict> dictList;
}

package com.fanxuankai.zeus.common.dict;

import com.fanxuankai.zeus.common.dict.vo.SysDictVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
public class DictEnumsModel {
    private String packageName;
    private String shortName;
    private String auth;
    private List<SysDictVO> dictVOList;
}

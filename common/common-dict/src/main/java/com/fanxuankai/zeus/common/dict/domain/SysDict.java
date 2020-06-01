package com.fanxuankai.zeus.common.dict.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
public class SysDict {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 类型id
     */
    private Long typeId;
    /**
     * 代码
     */
    private Integer code;
    /**
     * 英文名称
     */
    private String englishName;
    /**
     * 中文名称
     */
    private String chineseName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;
    /**
     * 修改日期
     */
    private LocalDateTime lastModifiedDate;
}

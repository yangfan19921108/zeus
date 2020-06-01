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
public class SysDictType {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 类型名
     */
    private String name;
    /**
     * 类型描述
     */
    private String description;
    /**
     * 创建日期
     */
    private LocalDateTime createDate;
    /**
     * 修改日期
     */
    private LocalDateTime lastModifiedDate;
}

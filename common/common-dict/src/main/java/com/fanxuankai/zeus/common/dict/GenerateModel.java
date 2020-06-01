package com.fanxuankai.zeus.common.dict;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 代码生成 model
 *
 * @author fanxuankai
 */
@Data
@Accessors(chain = true)
public class GenerateModel {
    /**
     * 作者
     */
    private String auth;
    /**
     * 全类名
     */
    private String className;
    /**
     * 文件路径
     */
    private String path;
}

package com.fanxuankai.zeus.common.data.jpa.utils;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.dangdang.ddframe.rdb.sharding.id.generator.self.CommonSelfIdGenerator;

/**
 * @author fanxuankai
 */
public class IdGeneratorUtil {

    private static final IdGenerator ID_GENERATOR = new CommonSelfIdGenerator();

    public static Number generateId() {
        return ID_GENERATOR.generateId();
    }
}

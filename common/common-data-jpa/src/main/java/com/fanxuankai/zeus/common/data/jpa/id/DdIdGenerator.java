package com.fanxuankai.zeus.common.data.jpa.id;

import com.fanxuankai.zeus.common.data.jpa.utils.IdGeneratorUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * @author fanxuankai
 */
public class DdIdGenerator extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        Number number = IdGeneratorUtil.generateId();
        if (number != null) {
            return number.longValue();
        }
        return super.generate(s, obj);
    }
}

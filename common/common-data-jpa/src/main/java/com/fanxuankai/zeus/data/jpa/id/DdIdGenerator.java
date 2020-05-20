package com.fanxuankai.zeus.data.jpa.id;

import com.fanxuankai.zeus.id.DangDangIdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * 当当网 ID 生成器
 *
 * @author fanxuankai
 */
public class DdIdGenerator extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        Number number = DangDangIdGenerator.generateId();
        if (number != null) {
            return number.longValue();
        }
        return super.generate(s, obj);
    }
}

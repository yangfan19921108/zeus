package com.fanxuankai.zeus.spring.context;

import com.fanxuankai.zeus.util.GenericTypeUtils;

/**
 * @author fanxuankai
 */
public class TypeReference<T> {
    protected final Class<T> type;

    protected TypeReference() {
        this.type = GenericTypeUtils.getGenericType(getClass(), TypeReference.class, 0);
    }

    public Class<T> getType() {
        return type;
    }

}

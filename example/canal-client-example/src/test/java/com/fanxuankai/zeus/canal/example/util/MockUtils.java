package com.fanxuankai.zeus.canal.example.util;

import com.fanxuankai.zeus.data.jpa.domain.BaseEntity;

import java.util.List;

public class MockUtils {

    public static <T extends BaseEntity> List<T> mock(int startInclusive, int endExclusive, Class<T> clazz) {
        List<T> list = com.fanxuankai.zeus.util.MockUtils.mock(startInclusive, endExclusive, clazz);
        list.forEach(t -> t.setId(null));
        return list;
    }
}

package com.fanxuankai.zeus.canal.client.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 反射工具类
 *
 * @author fanxuankai
 */
@Slf4j
public class ReflectionUtils {

    /**
     * 获取所有属性类型
     *
     * @param clazz 类
     * @return key: 属性名 value: 属性类型
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Class<?>> getAllFieldsType(Class<?> clazz) {
        Set<Field> allFields = org.reflections.ReflectionUtils.getAllFields(clazz);
        Map<String, Class<?>> fieldsTypeMap = new HashMap<>(allFields.size());
        for (Field field : allFields) {
            field.setAccessible(true);
            fieldsTypeMap.put(field.getName(), field.getType());
        }
        return fieldsTypeMap;
    }

}

package com.fanxuankai.zeus.util;

/**
 * @author fanxuankai
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 自动创建对象并复制源对象的属性
     *
     * @param source 源对象
     * @param t      目标对象类
     * @param <T>    目标对象类型
     * @return 目标对象
     * @throws RuntimeException 如果目标对象不能实例化
     */
    public static <T> T copyProperties(Object source, Class<T> t) {
        try {
            T target = t.getConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

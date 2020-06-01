package com.fanxuankai.zeus.common.dict.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * 字典枚举
 *
 * @author fanxuankai
 */
public class Dict {

    /**
     * 查字典
     *
     * @param enumClass 字典类型
     * @param code      代码
     * @param <E>       字典泛型
     * @return 可能为 Optional.empty()
     */
    public static <E extends Enum<?>> Optional<E> lookup(Class<E> enumClass, Integer code) {
        try {
            Field codeField = enumClass.getDeclaredField("code");
            codeField.setAccessible(true);
            for (E enumConstant : enumClass.getEnumConstants()) {
                if (Objects.equals(codeField.get(enumConstant), code)) {
                    return Optional.of(enumConstant);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
        return Optional.empty();
    }

    /**
     * 查字典
     *
     * @param enumClass 字典类型
     * @param code      代码
     * @param <E>       字典泛型
     * @return 可能为 null
     */
    public static <E extends Enum<?>> E get(Class<E> enumClass, Integer code) {
        return lookup(enumClass, code).orElse(null);
    }

    /**
     * 颜色
     */
    @AllArgsConstructor
    @Getter
    public enum Colour {
        /**
         * 白色
         */
        WHITE(0, "白色"),
        /**
         * 红色
         */
        RED(1, "红色"),
        /**
         * 黑色
         */
        BLACK(2, "黑色"),
        ;
        private final Integer code;
        private final String name;
    }

    /**
     * 是否删除
     */
    @AllArgsConstructor
    @Getter
    public enum Deleted {
        /**
         * 未删除
         */
        NO(0, "未删除"),
        /**
         * 已删除
         */
        YES(1, "已删除"),
        ;
        private final Integer code;
        private final String name;
    }

}
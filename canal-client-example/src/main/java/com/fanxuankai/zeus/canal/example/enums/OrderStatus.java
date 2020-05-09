package com.fanxuankai.zeus.canal.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author fanxuankai
 */

@Getter
@AllArgsConstructor
public enum OrderStatus {
    // 订单创建
    CREATED(0, "订单创建"),
    // 订单完成
    COMPLETED(1, "订单完成"),
    ;
    private final Integer code;
    private final String name;

    public static Optional<OrderStatus> ofNullable(Integer code) {
        return Arrays.stream(values())
                .filter(o -> Objects.equals(o.code, code))
                .findFirst();
    }

    public static OrderStatus of(Integer code) {
        return Arrays.stream(values())
                .filter(o -> Objects.equals(o.code, code))
                .findFirst()
                .orElse(null);
    }
}

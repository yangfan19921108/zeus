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
public enum RelationType {
    // a
    A(0, "A"),
    // b
    B(1, "B"),
    // c
    C(2, "C"),
    ;
    private final Integer code;
    private final String name;

    public static Optional<RelationType> ofNullable(Integer code) {
        return Arrays.stream(values())
                .filter(o -> Objects.equals(o.code, code))
                .findFirst();
    }

    public static RelationType of(Integer code) {
        return Arrays.stream(values())
                .filter(o -> Objects.equals(o.code, code))
                .findFirst()
                .orElse(null);
    }
}

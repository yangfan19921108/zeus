package ${packageName};

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * 字典
 *
 * @author ${auth}
 */
public class ${shortName} {

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
<#list dictVOList as dictVO>

    /**
     * ${dictVO.typeDescription}
     */
    @AllArgsConstructor
    @Getter
    public enum ${dictVO.typeName} {
    <#list dictVO.dictList as dict>
        /**
         * ${dict.chineseName}
         */
        ${dict.englishName}(${dict.code}, "${dict.chineseName}"),
    </#list>
        ;
        private final Integer code;
        private final String name;
    }
</#list>

}
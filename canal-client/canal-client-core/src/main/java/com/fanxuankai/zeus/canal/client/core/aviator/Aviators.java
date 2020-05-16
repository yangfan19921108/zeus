package com.fanxuankai.zeus.canal.client.core.aviator;

import com.google.common.base.CaseFormat;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Slf4j
public class Aviators {

    /**
     * key: 类 value: {字段名: 字段类型}
     */
    private static final Map<Class<?>, Map<String, Class<?>>> FIELDS_TYPE_CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 转换工具类
     */
    private static final ConversionService CONVERSION_SERVICE = Conversions.getInstance();

    /**
     * aviator 执行
     *
     * @param columnMap         数据行的所有列
     * @param aviatorExpression aviator 表达式
     * @param javaType          对应的 Java 类型
     * @return true or false
     * @throws ExpressionSyntaxErrorException 表达式返回boolean类型, 否则抛出异常
     */
    public static boolean exec(Map<String, String> columnMap, String aviatorExpression, Class<?> javaType) {
        Expression expression = AviatorEvaluator.compile(aviatorExpression, true);
        Object execute = expression.execute(env(columnMap, javaType));
        if (execute instanceof Boolean) {
            return (boolean) execute;
        }
        throw new ExpressionSyntaxErrorException("表达式语法错误: " + aviatorExpression);
    }

    /**
     * 数据库表格列转 Aviator env
     *
     * @param columnMap 列
     * @param javaType  Java 类型
     * @return key: 字段名 value: 字段值
     */
    private static Map<String, Object> env(Map<String, String> columnMap, Class<?> javaType) {
        return toActualType(columnMap, javaType, true);
    }

    /**
     * 数据库表格列转实际类型
     *
     * @param columnMap 列
     * @param javaType  Java 类型
     * @return key: 字段名 value: 字段值
     */
    public static Map<String, Object> toActualType(Map<String, String> columnMap, Class<?> javaType,
                                                   boolean localToDate) {
        Map<String, Class<?>> allFieldsType = getAllFieldsType(javaType);
        Map<String, Object> map = new HashMap<>(columnMap.size());
        for (Map.Entry<String, String> entry : columnMap.entrySet()) {
            String name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, entry.getKey());
            Object convert = null;
            if (!StringUtils.isBlank(entry.getValue())) {
                Class<?> fieldType = allFieldsType.get(name);
                if (localToDate) {
                    // Aviator 不支持 LocalDate 和 LocalDateTime 类型
                    // 直接把字符串当做 Date 来处理
                    fieldType = isLocalDateType(fieldType) ? Date.class : fieldType;
                }
                convert = CONVERSION_SERVICE.convert(entry.getValue(), fieldType);
            }
            map.put(name, convert);
        }
        return map;
    }

    private static boolean isLocalDateType(Class<?> fieldType) {
        return LocalDate.class.isAssignableFrom(fieldType)
                || LocalDateTime.class.isAssignableFrom(fieldType);
    }

    /**
     * 获取所有字段, 包括父类的字段
     *
     * @param clazz 类
     * @return key: 字段名 value: 字段类型
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Class<?>> getAllFieldsType(Class<?> clazz) {
        Map<String, Class<?>> fieldsTypeMap = FIELDS_TYPE_CLASS_MAP.get(clazz);
        if (fieldsTypeMap == null) {
            fieldsTypeMap = ReflectionUtils.getAllFields(clazz)
                    .stream()
                    .collect(Collectors.toMap(Field::getName, Field::getType));
            FIELDS_TYPE_CLASS_MAP.put(clazz, fieldsTypeMap);
        }
        return fieldsTypeMap;
    }

}

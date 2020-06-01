### 简介
通常项目中由于业务需要，需要创建枚举类，部分项目中数量庞大；

项目中枚举格式大同小异，这也就造成大量的重复代码；

那有没有好的方法来避免重复代码的编写，通过一个统一的类来访问系统中定义的枚举？

### 实现原理
基于数据库来实现，数据库新增字典表，插入需要使用的字典数据，最后使用 freemarker 生成枚举，

字典需要修改时，只需要修改字典表数据，然后重新生成代码。

### Getting started
- 建表
```
drop table if exists `sys_dict_type`;
drop table if exists `sys_dict`;
CREATE TABLE `sys_dict_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `last_modified_date` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典类型表';

CREATE TABLE `sys_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_id` int(11) DEFAULT NULL COMMENT '类型id',
  `code` tinyint(4) DEFAULT NULL COMMENT '代码',
  `english_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '英文名称',
  `chinese_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '中文名称',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `last_modified_date` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_id`,`code`),
  UNIQUE KEY `uk_type_en_name` (`type_id`,`english_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典表';
```
- 添加依赖
```
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>common-dict</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>
```
- Usage
```
@Resource
private SysDictService sysDictService;
@Resource
private DictGenerator dictGenerator;

// 创建颜色字典
List<SysDict> list = new ArrayList<>(3);
list.add(new SysDict().setEnglishName("white").setChineseName("白色"));
list.add(new SysDict().setEnglishName("red").setChineseName("红色"));
list.add(new SysDict().setEnglishName("black").setChineseName("黑色"));
sysDictService.addAll(new SysDictType().setName("colour").setDescription("颜色"), list, true);

// 创建是否删除字典
list = new ArrayList<>(2);
list.add(new SysDict().setEnglishName("no").setChineseName("未删除"));
list.add(new SysDict().setEnglishName("yes").setChineseName("已删除"));
sysDictService.addAll(new SysDictType().setName("deleted").setDescription("是否删除"), list, true);

// 生成字典类
dictGenerator.generate(new GenerateModel()
                .setAuth("fanxuankai")
                .setPath("/Users/fanxuankai/Java/Workspace/myproject/fanxuankai/zeus" +
                        "/example/common-dict-example/src/test/java")
                .setClassName("com.fanxuankai.zeus.common.dict.example.Dict"));
```

### 字典类
```
package com.fanxuankai.zeus.common.dict.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * 字典
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
```

### 字典使用
```
// 查找是否删除
Dict.lookup(Deleted.class, 0).ifPresent(System.out::println);
// 查找颜色
System.out.println(Dict.get(Colour.class, 0));
```

详见：[common-dict-example](https://github.com/fanxuankai/zeus/tree/master/example/common-dict-example)
### 简介
系统词典, 以枚举的形式存在

### Getting started
- 建表
```
drop table if exists `sys_dict_type`;
drop table if exists `sys_dict`;
CREATE TABLE `sys_dict_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典类型表';

CREATE TABLE `sys_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) DEFAULT NULL,
  `code` tinyint(4) DEFAULT NULL,
  `english_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `chinese_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_id`,`code`),
  UNIQUE KEY `uk_type_en_name` (`type_id`,`english_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典表';
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
private DictGenerator dictGenerator;

dictGenerator.generate(new GenerateModel()
                .setAuth("fanxuankai")
                .setPath("/Users/fanxuankai/Java/Workspace/myproject/fanxuankai/zeus" +
                        "/example/common-dict-example/src/test/java")
                .setClassName("com.fanxuankai.zeus.common.dict.example.Dict"));
```
详见：[common-dict-example](https://github.com/fanxuankai/zeus/tree/master/example/common-dict-example)
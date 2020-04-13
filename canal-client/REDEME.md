## 简介

![](http://processon.com/chart_image/5e7d8f17e4b08e4e24428c33.png)
https://www.processon.com/view/link/5e7d8f28e4b08e4e24428c6c

### 功能
- Redis 缓存刷新, 支持增量和全量
- 数据感知 MQ 推送, 目前支持 RabbitMQ、XXL-MQ
- 构建 Elasticsearch 索引 （暂未支持）

### 环境
- Canal 服务
- JDK 14

### 核心注解
@EnableCanal
- 在 spring boot 启动类使用, 表示激活 Canal 消费
- defaultSchema 默认数据库名

### 配置文件
```
## application.yml
canal:
  application-name: admin-service
  redis:
    # 对应的 Canal 实例名
    instance: redis-example
    #enabled: true
  rabbit:
    instance: rabbit-example
    # MQ 跳过处理, 适用场景: Redis 全量同步时, MQ 跳过
    #skip: false
    #enabled: true
  xxl:
    instance: xxl-example
    #skip: false
    #enabled: true
  # 集群配置
  #cluster:
    # zookeeper的ip+端口, 以逗号隔开
    #nodes: localhost:2181,localhost:2182,localhost:2183
  # 单节点配置
  #single-node:
    # ip
    #hostname: localhost
    # 端口
    #port: 11111
  # 账号
  #username: canal
  # 密码
  #password: canal
  # 间隔
  #interval-millis: 1000
  # 批次数量
  #batch-size: 100
  # 打印日志
  #show-log: false
  # 打印数据明细日志
  #show-row-change: false
  # 格式化数据明细日志
  #format-row-change-log: false
  # 批次达到一定数量进行并行处理, 且确保顺序消费
  #performance-threshold: 10000
  # 暂不支持集群, 设置该参数为 true 时, 当启动了 Canal 服务的节点停止后可以及时补位
  #retry-start: true
  # retryStart 的间隔秒数
  #retry-start-interval-seconds = 300L
```

### 使用指南
- maven 添加相关 canal-client 依赖
- SpringBoot 启动类标注 @EnableCanal
- 依赖多个 canal-client, @CanalEnable 只需要一次注解

#### Redis 快速使用
- pom.xml 添加以下配置
```
<!-- Redis -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-redis</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 XXXRedisRepository 接口, 继承 RedisRepository<XXX>

#### RabbitMQ 快速使用
- pom.xml 添加以下配置
```
<!-- RabbitMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-rabbit</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 MqConsumer<XXX> 的实现类

#### XxlMQ 快速使用
- pom.xml 添加以下配置
```
<!-- XxlMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-xxl</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 MqConsumer<XXX> 的实现类

### 常见问题
- 已有实例建立了 Canal 链接？
    - 清除 Redis 标记重启即可, key 的格式为 Canal.ServiceCache.服务名.CanalRunning
- exception=com.alibaba.otter.canal.meta.exception.CanalMetaManagerException: batchId:845 is not the firstly:844
    - 如果同时激活了 Redis、Mq, 且两者共用同一个 Canal 实例, 可能会导致 batchId 提交顺序错误
    - 建议两者使用独立的 Canal 实例 
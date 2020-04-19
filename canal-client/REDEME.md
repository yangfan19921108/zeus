## 简介
一个轻量级、可扩展、个性化、非侵入式的 Canal Client;
<br/>
与 Spring Boot 无缝对接, application 配置修改无需重启服务;
<br/>
数据消费实现了数据选择性、高性能、顺序性、防重;
<br/>
目前支持的数据库有 Redis, 消息队列有 RabbitMQ、XXL-MQ;
<br/>
理论上可以实现同步到任何数据库或消息队列, 实现最少只需要三步: 定义注解、消费实现、启动 Canal 工作线程.
<br/>
![](http://assets.processon.com/chart_image/5e7d8f17e4b08e4e24428c33.png)
https://www.processon.com/view/link/5e7d8f28e4b08e4e24428c6c

### 场景
- 同步缓存 redis
- 全文搜索 es
- 任务下发, 比如: 订单完成统计报表、商品价格变化更新商品详情页.

### 环境
- Canal 服务
- JDK 14
- Redis

### 核心注解
@EnableCanal
- 在 spring boot 启动类使用, 表示激活 Canal 消费
- schema 默认数据库名

### 配置文件
```
## application.yml
canal:
  application-name: example-service
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
  #拉取数据阻塞时间 ms
  #一般情况下是不需要的, 会造成阻塞
  #数据量大的时候, 可调此值减少 IO 次数
  # timeout-millis:
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
  #retry-start-interval-seconds: 300
```

### 使用指南
- maven 添加相关 canal-client 依赖
- SpringBoot 启动类标注 @EnableCanal
- 依赖多个 canal-client, @EnableCanal 只需要一次注解
- 数据库对应的实体类使用 @CanalTable 指定数据库和表名等信息

#### Redis 快速使用
- pom.xml 添加以下配置
```
<!-- Redis -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-redis</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 XXXRedisRepository 接口, 继承 RedisRepository<XXX>
- 使用 @CanalToRedis 注解指定消费 (可选)

#### RabbitMQ 快速使用
- pom.xml 添加以下配置
```
<!-- RabbitMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-rabbit</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 MqConsumer<XXX> 的实现类, 并作为 Spring Bean
- 使用 @CanalToMq 注解指定消费 (可选)

#### XxlMQ 快速使用
- pom.xml 添加以下配置
```
<!-- XxlMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-xxl</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>
```
- SpringBoot 启动类标注 @EnableCanal
- 创建 MqConsumer<XXX> 的实现类, 并作为 Spring Bean
- 使用 @CanalToMq 注解指定消费 (可选)

### 常见问题
- Canal.ServiceCache.admin-service.Redis.CanalRunning Canal 已存在?
    - Redis 删掉 key: Canal.ServiceCache.admin-service.Redis.CanalRunning, 重启或等待重试即可
- exception=com.alibaba.otter.canal.meta.exception.CanalMetaManagerException: batchId:845 is not the firstly:844
    - 如果同时激活了 Redis、Mq, 且两者共用同一个 Canal 实例, 可能会导致 batchId 提交顺序错误
    - 建议两者使用独立的 Canal 实例 
- 删表时, Redis 暂支持单表删除.
 一次删除一张表 Redis 可以正常同步, 一次删除多张表 Redis 只同步第一张表.
- canal 不支持数据库 bit 格式
- 同步到 Redis 时用组合键, 发现 mysql 与 Redis 数量不对
    - 原因是数据库排序规则不区分大小写, 导致 mysql 的数量比 Redis 少.
    - 修改为 CHARSET=utf8mb4 COLLATE=utf8mb4_bin, 验证结果完全一致.  
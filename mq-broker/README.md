### 简介
增强消息队列可靠性，消息一定发送成功且只发送一次，消息一定接收成功且只消费一次。

### 原理
![](http://processon.com/chart_image/5ec55550f346fb6907090118.png)

### 发送端的可靠性
本地创建发送消息表并具有唯一编号，利用本地事务机制持久化，待事务提交成功，将未发送的消息发送至消息队列，失败则重试到一定次数，成功则修改状态为已发送或者删除。
### 接收端的可靠性
本地创建接收消息表并具有唯一编号，接收到消息队列的消息后利用本地事务机制持久化，待事务提交成功，将未消费的消息分配给对应的消费者，失败则重试到一定次数，成功则修改状态为已消费或者删除。

### 支持消息队列
- RabbitMQ
- XXL-MQ

### Getting started
- 建表
```
drop table if exists `mq_broker_lock`;
drop table if exists `mq_broker_msg_send`;
drop table if exists `mq_broker_msg_receive`;
CREATE TABLE `mq_broker_msg_send` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '队列名',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '代码',
  `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '内容',
  `status` int(1) NOT NULL COMMENT '状态 0:已创建 1:运行中 2:成功 3:失败',
  `host_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主机地址',
  `retry` int(1) NOT NULL DEFAULT '0' COMMENT '重试次数',
  `cause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '失败原因',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `last_modified_date` datetime NOT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_code` (`topic`,`code`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='发送消息表';

CREATE TABLE `mq_broker_msg_receive` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '队列名',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '代码',
  `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '内容',
  `status` int(1) NOT NULL COMMENT '状态 0:已创建 1:运行中 2:成功 3:失败',
  `host_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主机地址',
  `retry` int(1) NOT NULL DEFAULT '0' COMMENT '重试次数',
  `cause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '失败原因',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `last_modified_date` datetime NOT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_code` (`topic`,`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='接收消息表';

CREATE TABLE `mq_broker_lock` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '资源',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource` (`resource`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='分布式锁';
```
- 添加 maven 依赖, 选择一种消息队列
```
<!-- RabbitMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>mq-broker-rabbit</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>

<!-- XXL-MQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>mq-broker-xxl</artifactId>
    <version>${com.fanxuankai.zeus.version}</version>
</dependency>
```
- 监听事件
```
@Service
@Listener(event = "user")
public class UserEventListener implements EventListener {

    @Override
    public void onEvent(Event event) {
        // do something
    }

}
```
- Usage
```
@Resource
private EventPublisher eventPublisher;

eventPublisher.publish(IntStream.range(0, 100)
                .mapToObj(value -> new Event()
                        .setName("user")
                        .setKey(UUID.randomUUID().toString())
                        .setData("fanxuankai"))
                .collect(Collectors.toList()));
```
### 参数配置
```
## application.yml
zeus:
  mq-broker:
    # 最大重试次数
    #max-retry: 3
    # 拉取数据的间隔 ms
    #interval-millis: 1000
    # 拉取发送消息的数量
    #msg-send-pull-data-count: 1000
    # 拉取接收消息的数量
    #msg-receive-pull-data-count: 500
    # 拉取发送消息分布式锁超时时间 ms
    #msg-send-lock-timeout: 30000
    # 拉取接收消息分布式锁超时时间 ms
    #msg-receive-lock-timeout: 60000
    # 发布回调超时
    #publisher-callback-timeout: 20000
    # 发布回调超时处理分布式锁超时时间 ms
    #publisher-callback-lock-timeout: 300000
    # 事件策略
    #event-strategy:
      # key: 消息队列 value: EventStrategy
      #user: DEFAULT
```
### 简介
消息队列可靠性增强，消息一定发送成功且只发送一次，消息一定接收成功且只消费一次。

### 原理
![](http://processon.com/chart_image/5ec55550f346fb6907090118.png?_=1590034254430)

### 处理流程
![](http://processon.com/chart_image/5ec29045e401fd16f4445959.png?_=1590034271735)

### 发送端的可靠性
本地创建发送消息表并具有唯一编号，利用本地事务机制持久化，待事务提交成功，将未发送的消息发送至消息队列，失败则重试到一定次数，成功则修改状态为已发送或者删除。
### 接收端的可靠性
本地创建接收消息表并具有唯一编号，接收到消息队列的消息后利用本地事务机制持久化，待事务提交成功，将未消费的消息分配给对应的消费者，失败则重试到一定次数，成功则修改状态为已消费或者删除。

### 支持消息队列
- RabbitMQ
- XXL-MQ

### Getting started
- 创建消息表
```
CREATE TABLE `mq_broker_message` (
  `id` bigint(12) NOT NULL COMMENT '主键',
  `type` int(1) DEFAULT NULL COMMENT '类型 1:发送 2:接收',
  `queue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '队列名',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '唯一代码',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '内容',
  `status` int(1) DEFAULT NULL COMMENT '状态 0:已创建 1:运行中 2:成功 3:失败',
  `host_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主机地址',
  `retry` int(1) DEFAULT '0' COMMENT '重试次数',
  `error` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '失败原因',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `last_modified_date` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_type_queue_code` (`type`,`queue`,`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
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
    - 依赖注入 
    ```
      @Resource
      private EventPublisher eventPublisher;
    ```
    - 使用
    ```
      eventPublisher.publish(IntStream.range(0, 10000)
          .mapToObj(value -> new Event("user", UUID.randomUUID().toString(), jsonString))
          .collect(Collectors.toList()));
    ```

### 参数配置
```
## application.yml
zeus:
  mq-broker:
    # 最大重试次数
    #max-retry: 6
    # 工作线程睡眠时间 ms
    #interval-millis: 1000
    # 抓取数据的数量
    #batch-count: 100
    # 事件监听策略
    #event-listener-strategy:
      # key: 消息队列 value: EventListenerStrategy
      #user: DEFAULT
```
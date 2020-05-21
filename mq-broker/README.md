### 简介
消息队列可靠性增强，消息一定发送成功且只发送一次，消息一定接收成功且只消费一次。

### 模型
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
    - Spring Boot 启动类添加注解 @EnableMqBroker
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
    # 生产频率 ms
    #produce-interval-millis: 100
    # 消费频率 ms
    #consume-interval-millis: 100
    # 一次性取发送消息表的数量
    #produce-batch-count: 500
    # 一次性取接收消息表的数量
    #consume-batch-count: 500
    # 事件监听策略
    #event-listener-strategy:
      # key: 消息队列 value: EventListenerStrategy
      #user: DEFAULT
```
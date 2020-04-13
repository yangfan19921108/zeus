## ���

![](http://processon.com/chart_image/5e7d8f17e4b08e4e24428c33.png)
https://www.processon.com/view/link/5e7d8f28e4b08e4e24428c6c

### ����
- Redis ����ˢ��, ֧��������ȫ��
- ���ݸ�֪ MQ ����, Ŀǰ֧�� RabbitMQ��XXL-MQ
- ���� Elasticsearch ���� ����δ֧�֣�

### ����
- Canal ����
- JDK 14

### ����ע��
@EnableCanal
- �� spring boot ������ʹ��, ��ʾ���� Canal ����
- defaultSchema Ĭ�����ݿ���

### �����ļ�
```
## application.yml
canal:
  application-name: admin-service
  redis:
    # ��Ӧ�� Canal ʵ����
    instance: redis-example
    #enabled: true
  rabbit:
    instance: rabbit-example
    # MQ ��������, ���ó���: Redis ȫ��ͬ��ʱ, MQ ����
    #skip: false
    #enabled: true
  xxl:
    instance: xxl-example
    #skip: false
    #enabled: true
  # ��Ⱥ����
  #cluster:
    # zookeeper��ip+�˿�, �Զ��Ÿ���
    #nodes: localhost:2181,localhost:2182,localhost:2183
  # ���ڵ�����
  #single-node:
    # ip
    #hostname: localhost
    # �˿�
    #port: 11111
  # �˺�
  #username: canal
  # ����
  #password: canal
  # ���
  #interval-millis: 1000
  # ��������
  #batch-size: 100
  # ��ӡ��־
  #show-log: false
  # ��ӡ������ϸ��־
  #show-row-change: false
  # ��ʽ��������ϸ��־
  #format-row-change-log: false
  # ���δﵽһ���������в��д���, ��ȷ��˳������
  #performance-threshold: 10000
  # �ݲ�֧�ּ�Ⱥ, ���øò���Ϊ true ʱ, �������� Canal ����Ľڵ�ֹͣ����Լ�ʱ��λ
  #retry-start: true
  # retryStart �ļ������
  #retry-start-interval-seconds = 300L
```

### ʹ��ָ��
- maven ������ canal-client ����
- SpringBoot �������ע @EnableCanal
- ������� canal-client, @CanalEnable ֻ��Ҫһ��ע��

#### Redis ����ʹ��
- pom.xml �����������
```
<!-- Redis -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-redis</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot �������ע @EnableCanal
- ���� XXXRedisRepository �ӿ�, �̳� RedisRepository<XXX>

#### RabbitMQ ����ʹ��
- pom.xml �����������
```
<!-- RabbitMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-rabbit</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot �������ע @EnableCanal
- ���� MqConsumer<XXX> ��ʵ����

#### XxlMQ ����ʹ��
- pom.xml �����������
```
<!-- XxlMQ -->
<dependency>
    <groupId>com.fanxuankai.zeus</groupId>
    <artifactId>canal-client-xxl</artifactId>
    <version>${canal.client-version}</version>
</dependency>
```
- SpringBoot �������ע @EnableCanal
- ���� MqConsumer<XXX> ��ʵ����

### ��������
- ����ʵ�������� Canal ���ӣ�
    - ��� Redis �����������, key �ĸ�ʽΪ Canal.ServiceCache.������.CanalRunning
- exception=com.alibaba.otter.canal.meta.exception.CanalMetaManagerException: batchId:845 is not the firstly:844
    - ���ͬʱ������ Redis��Mq, �����߹���ͬһ�� Canal ʵ��, ���ܻᵼ�� batchId �ύ˳�����
    - ��������ʹ�ö����� Canal ʵ�� 
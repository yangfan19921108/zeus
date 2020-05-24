package com.fanxuankai.zeus.canal.client.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * canal参数配置
 *
 * @author fanxuankai
 */
@Configuration
@ConfigurationProperties(prefix = CanalProperties.PREFIX)
@Getter
@Setter
public class CanalProperties {

    public static final String PREFIX = "zeus.canal";

    /**
     * 应用名,暂不支持集群, 集群时只开启一个服务.
     */
    private String applicationName;

    /**
     * 集群配置
     */
    private Cluster cluster;

    /**
     * 单节点配置
     */
    private SingleNode singleNode = new SingleNode();

    /**
     * 账号
     */
    private String username = "canal";

    /**
     * 密码
     */
    private String password = "canal";

    /**
     * 拉取数据的间隔 ms
     */
    private long intervalMillis = 1_000;

    /**
     * 拉取数据的数量
     */
    private int batchSize = 100;

    /**
     * 打印事件日志
     */
    private boolean showEventLog;

    /**
     * 打印 Entry 日志
     */
    private boolean showEntryLog;

    /**
     * 打印数据明细日志
     */
    private boolean showRowChange;

    /**
     * 格式化数据明细日志
     */
    private boolean formatRowChangeLog;

    /**
     * 批次达到一定数量进行并行处理, 且确保顺序消费
     */
    private int performanceThreshold = 10_000;

    /**
     * 尝试启动
     */
    private boolean retryStart = true;

    /**
     * 尝试启动间隔 s
     */
    private long retryStartIntervalSeconds = 300;

    @Getter
    @Setter
    public static class Cluster {
        /**
         * zookeeper host:port
         */
        private String nodes = "localhost:2181,localhost:2182,localhost:2183";
    }

    @Getter
    @Setter
    public static class SingleNode {
        /**
         * host
         */
        private String hostname = "localhost";
        /**
         * port
         */
        private int port = 11111;
    }
}

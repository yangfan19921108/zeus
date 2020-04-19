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
@ConfigurationProperties(prefix = CanalConfig.PREFIX)
@Getter
@Setter
public class CanalConfig {

    public static final String PREFIX = "canal";

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
    private Long intervalMillis = 1000L;

    /**
     * 拉取数据的数量
     */
    private Integer batchSize = 100;

    /**
     * 拉取数据超时时间 ms
     */
    private Long timeoutMillis;

    /**
     * 打印日志
     */
    private Boolean showLog = Boolean.FALSE;

    /**
     * 显示数据变动的日志
     */
    private Boolean showRowChange = Boolean.FALSE;

    /**
     * 美化数据变动的日志
     */
    private Boolean formatRowChangeLog = Boolean.FALSE;

    /**
     * 批次达到一定数量进行并行处理, 且确保顺序消费
     */
    private Integer performanceThreshold = 10000;

    /**
     * 暂不支持集群, 设置该参数为 true 时, 当启动了 Canal 服务的节点停止后可以及时补位
     */
    private Boolean retryStart = Boolean.TRUE;

    /**
     * retryStart 的间隔秒数
     */
    private Long retryStartIntervalSeconds = 300L;

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
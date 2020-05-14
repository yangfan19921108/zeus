package com.fanxuankai.zeus.canal.client.core.util;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.fanxuankai.zeus.canal.client.core.config.CanalProperties;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Canal 链接工具类, 线程隔离
 *
 * @author fanxuankai
 */
@Slf4j
public class CanalConnectorHolder {

    /**
     * 默认重试次数
     */
    private static final int DEFAULT_RETRY_COUNT = 20;
    /**
     * 链接实例
     */
    private static final ThreadLocal<CanalConnector> CONNECTOR_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 创建链接
     *
     * @param connectConfig   链接配置参数
     * @param canalProperties Canal 配置参数
     */
    public static void connect(ConnectConfig connectConfig, CanalProperties canalProperties) {
        CanalConnector canalConnector = CONNECTOR_THREAD_LOCAL.get();
        if (canalConnector == null) {
            String instance = connectConfig.getInstance();
            if (canalProperties.getCluster() != null && !StringUtils.isEmpty(canalProperties.getCluster().getNodes())) {
                canalConnector = CanalConnectors.newClusterConnector(canalProperties.getCluster().getNodes(),
                        instance, canalProperties.getUsername(), canalProperties.getPassword());
            } else {
                canalConnector =
                        CanalConnectors.newSingleConnector(
                                new InetSocketAddress(canalProperties.getSingleNode().getHostname(),
                                        canalProperties.getSingleNode().getPort()), instance,
                                canalProperties.getUsername(),
                                canalProperties.getPassword());
            }
            canalConnector.connect();
            int retry = 0;
            boolean subscribeSuccess = false;
            // 异常后重试
            while (retry++ < DEFAULT_RETRY_COUNT) {
                try {
                    canalConnector.subscribe(connectConfig.getFilter());
                    canalConnector.rollback();
                    subscribeSuccess = true;
                    break;
                } catch (CanalClientException e) {
                    log.error(e.getLocalizedMessage(), e);
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception ex) {
                        log.error(e.getLocalizedMessage(), ex);
                    }
                }
            }
            if (subscribeSuccess) {
                CanalConnectorHolder.CONNECTOR_THREAD_LOCAL.set(canalConnector);
            } else {
                throw new RuntimeException(connectConfig.getApplicationInfo().uniqueString() + " Canal subscribe " +
                        "error");
            }
        }
    }

    public static void reconnect(ConnectConfig connectConfig, CanalProperties canalProperties) {
        CONNECTOR_THREAD_LOCAL.remove();
        connect(connectConfig, canalProperties);
    }

    public static void disconnect() {
        CanalConnector canalConnector = CONNECTOR_THREAD_LOCAL.get();
        if (canalConnector != null) {
            canalConnector.unsubscribe();
            canalConnector.disconnect();
            CONNECTOR_THREAD_LOCAL.remove();
        }
    }

    public static CanalConnector get() {
        return CONNECTOR_THREAD_LOCAL.get();
    }
}

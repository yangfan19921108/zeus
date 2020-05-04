package com.fanxuankai.zeus.canal.client.core.flow;

import com.fanxuankai.zeus.canal.client.core.config.CanalConfig;
import com.fanxuankai.zeus.canal.client.core.model.ApplicationInfo;
import com.fanxuankai.zeus.canal.client.core.model.ConnectConfig;
import com.fanxuankai.zeus.canal.client.core.model.ConsumerInfo;
import com.fanxuankai.zeus.canal.client.core.protocol.Handler;
import com.fanxuankai.zeus.canal.client.core.wrapper.MessageWrapper;
import lombok.Builder;
import lombok.Getter;

/**
 * @author fanxuankai
 */
@Builder
@Getter
public class Config {
    private final ApplicationInfo applicationInfo;
    private final CanalConfig canalConfig;
    private final ConnectConfig connectConfig;
    private final ConsumerInfo consumerInfo;
    private final Handler<MessageWrapper> handler;
    private final boolean skip;
}
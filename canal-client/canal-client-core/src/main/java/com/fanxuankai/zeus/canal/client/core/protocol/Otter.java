package com.fanxuankai.zeus.canal.client.core.protocol;

import com.fanxuankai.zeus.canal.client.core.model.Context;

/**
 * Otter 客户端接口
 *
 * @author fanxuankai
 */
public interface Otter {

    /**
     * 开启
     */
    void start();

    /**
     * 处理
     *
     * @param context 上下文
     */
    void process(Context context);

    /**
     * 停止
     */
    void stop();

}

package com.fanxuankai.zeus.canal.client.core.protocol;

/**
 * 消费者接口
 *
 * @author fanxuankai
 */
public interface Consumer<T, R> {

    /**
     * 是否能够处理
     *
     * @param t 数据
     * @return true or false
     */
    boolean canProcess(T t);

    /**
     * 转换为需要的数据
     *
     * @param t 数据
     * @return R
     */
    R process(T t);

    /**
     * 消费
     *
     * @param r 数据
     */
    void consume(R r);
}

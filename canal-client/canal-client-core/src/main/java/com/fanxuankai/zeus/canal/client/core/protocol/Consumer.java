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
     * 是否可以过滤.
     * 通常情况下是可以过滤的, 但是缓存减量时不应该过滤, 为了保证数据一致性, 数据库的删除事件必须消费.
     *
     * @return true: @Filter 生效, false: @Filter 失效
     */
    default boolean filterable() {
        return true;
    }

    /**
     * 消费
     *
     * @param r 数据
     */
    void consume(R r);
}

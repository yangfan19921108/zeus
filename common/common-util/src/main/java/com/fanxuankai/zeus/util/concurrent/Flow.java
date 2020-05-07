package com.fanxuankai.zeus.util.concurrent;

/**
 * @author fanxuankai
 */
public class Flow {

    public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {

    }

    public interface Publisher<T> {
        /**
         * 订阅
         *
         * @param subscriber 订阅者
         */
        void subscribe(Subscriber<? super T> subscriber);
    }

    public interface Subscriber<T> {
        /**
         * on next
         *
         * @param item the item
         */
        void onNext(T item);

        /**
         * on complete
         */
        default void onComplete() {

        }

        /**
         * on error
         *
         * @param throwable the throwable
         */
        void onError(Throwable throwable);
    }
}

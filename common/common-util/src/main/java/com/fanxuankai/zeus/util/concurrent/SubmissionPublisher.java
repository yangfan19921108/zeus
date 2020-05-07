package com.fanxuankai.zeus.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author fanxuankai
 */
public class SubmissionPublisher<T> implements Flow.Publisher<T>, AutoCloseable {
    private final List<Flow.Subscriber<? super T>> subscribers = new ArrayList<>();
    private final BlockingQueue<T> queue = new ArrayBlockingQueue<>(256);
    private volatile boolean stop;

    public SubmissionPublisher() {
        consume();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void submit(T item) {
        try {
            queue.put(item);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        stop();
        this.subscribers.forEach(Flow.Subscriber::onComplete);
        this.subscribers.clear();
    }

    protected void stop() {
        this.stop = true;
    }

    private void consume() {
        ThreadPoolService.getInstance().execute(() -> {
            while (!stop) {
                try {
                    T item = queue.take();
                    for (Flow.Subscriber<? super T> subscriber : subscribers) {
                        try {
                            subscriber.onNext(item);
                        } catch (Throwable throwable) {
                            subscriber.onError(throwable);
                            this.stop();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

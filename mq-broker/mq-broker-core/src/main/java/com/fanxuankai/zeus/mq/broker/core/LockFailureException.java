package com.fanxuankai.zeus.mq.broker.core;

/**
 * @author fanxuankai
 */
public class LockFailureException extends RuntimeException {
    public LockFailureException(String message) {
        super(message);
    }
}

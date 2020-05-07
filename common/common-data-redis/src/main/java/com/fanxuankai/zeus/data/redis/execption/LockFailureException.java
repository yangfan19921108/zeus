package com.fanxuankai.zeus.data.redis.execption;

/**
 * @author fanxuankai
 */
public class LockFailureException extends RuntimeException {
    public LockFailureException() {
    }

    public LockFailureException(Throwable cause) {
        super(cause);
    }
}

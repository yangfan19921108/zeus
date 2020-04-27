package com.fanxuankai.zeus.common.data.redis.execption;

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

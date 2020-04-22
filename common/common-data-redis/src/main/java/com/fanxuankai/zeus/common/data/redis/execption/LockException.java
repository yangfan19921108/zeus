package com.fanxuankai.zeus.common.data.redis.execption;

/**
 * @author fanxuankai
 */
public class LockException extends RuntimeException {
    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}

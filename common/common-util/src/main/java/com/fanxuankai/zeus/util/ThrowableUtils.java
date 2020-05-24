package com.fanxuankai.zeus.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author fanxuankai
 */
public class ThrowableUtils {
    /**
     * 获取错误的堆栈信息
     *
     * @param throwable 异常
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}

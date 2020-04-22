package com.fanxuankai.zeus.common.aop.util.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.fanxuankai.zeus.common.aop.util.annotation.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author fanxuankai
 */
@Slf4j
public class LogMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            return proceed(methodInvocation, logAnnotation);
        }
        return methodInvocation.proceed();
    }

    private Object proceed(MethodInvocation methodInvocation, Log logAnnotation) throws Throwable {
        LogInfo logInfo = new LogInfo();

        long start = System.currentTimeMillis();
        Object proceed = methodInvocation.proceed();
        if (logAnnotation.executeTime()) {
            logInfo.timeMillis = System.currentTimeMillis() - start;
        }

        Method method = methodInvocation.getMethod();
        logInfo.className = method.getDeclaringClass().getName();
        logInfo.methodName = method.getName();
        if (logAnnotation.params() && method.getParameters() != null) {
            try {
                logInfo.params = JSON.toJSONString(method.getParameters());
            } catch (JSONException ignored) {

            }
        }
        if (logAnnotation.returnValue() && proceed != null) {
            try {
                logInfo.returnValue = JSON.toJSONString(proceed);
            } catch (JSONException ignored) {

            }
        }
        log.info(JSON.toJSONString(logInfo, true));
        return proceed;
    }

    @Getter
    @Setter
    private static final class LogInfo {
        private String className;
        private String methodName;
        private String params;
        private String returnValue;
        private long timeMillis;
    }
}

package com.fanxuankai.zeus.common.aop.util.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.fanxuankai.zeus.common.aop.util.annotation.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxuankai
 */
@Slf4j
public class LogMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        return proceed(methodInvocation, logAnnotation);
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
        if (logAnnotation.params()) {
            try {
                logInfo.setParams(methodInvocation);
            } catch (JSONException ignored) {

            }
        }
        if (logAnnotation.returnValue()) {
            try {
                logInfo.returnValue = proceed;
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
        private Map<String, Object> params;
        private Object returnValue;
        private long timeMillis;

        public void setParams(MethodInvocation methodInvocation) {
            Object[] arguments = methodInvocation.getArguments();
            Parameter[] parameters = methodInvocation.getMethod().getParameters();
            if (arguments != null && arguments.length > 0) {
                params = new HashMap<>(arguments.length);
                for (int i = 0; i < parameters.length; i++) {
                    params.put(parameters[i].getName(), arguments[i]);
                }
            }
        }
    }
}

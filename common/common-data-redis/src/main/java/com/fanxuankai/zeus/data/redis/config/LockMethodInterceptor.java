package com.fanxuankai.zeus.data.redis.config;

import com.fanxuankai.zeus.data.redis.annotation.Lock;
import com.fanxuankai.zeus.data.redis.lock.DistributedLocker;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * @author fanxuankai
 */
public class LockMethodInterceptor implements MethodInterceptor {

    private final DistributedLocker distributedLocker;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    public LockMethodInterceptor(DistributedLocker distributedLocker) {
        this.distributedLocker = distributedLocker;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Lock lock = methodInvocation.getMethod().getAnnotation(Lock.class);
        return distributedLocker.lock(getKey(lock, methodInvocation), lock.waitTimeMillis(),
                lock.leaseTimeMillis(), () -> {
                    try {
                        return methodInvocation.proceed();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                });
    }

    private String getKey(Lock lock, MethodInvocation methodInvocation) {
        String key = getBusiness(lock, methodInvocation);
        Optional<String> resourceOptional = getResource(lock, methodInvocation);
        if (resourceOptional.isPresent()) {
            key = key + "." + resourceOptional.get();
        }
        return key;
    }

    private String getBusiness(Lock lock, MethodInvocation methodInvocation) {
        String business = lock.business();
        if (StringUtils.isEmpty(business)) {
            Method method = methodInvocation.getMethod();
            business = method.getDeclaringClass().getName() + "." + method.getName();
        }
        return business;
    }

    private Optional<String> getResource(Lock lock, MethodInvocation methodInvocation) {
        String expression = lock.resource();
        if (expression.isBlank()) {
            return Optional.empty();
        }
        SpelExpression spelExpression = parser.parseRaw(expression);
        EvaluationContext context = new StandardEvaluationContext();
        Method method = methodInvocation.getMethod();
        Object[] arguments = methodInvocation.getArguments();
        Parameter[] parameters = method.getParameters();
        if (arguments != null && arguments.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), arguments[i]);
            }
        }
        return Optional.ofNullable(spelExpression.getValue(context)).map(Object::toString);
    }

}

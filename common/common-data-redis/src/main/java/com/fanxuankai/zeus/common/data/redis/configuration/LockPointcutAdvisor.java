package com.fanxuankai.zeus.common.data.redis.configuration;

import com.fanxuankai.zeus.common.data.redis.aop.LockMethodInterceptor;
import com.fanxuankai.zeus.common.data.redis.lock.DistributedLocker;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author fanxuankai
 */
public class LockPointcutAdvisor extends DefaultPointcutAdvisor {
    public LockPointcutAdvisor(DistributedLocker distributedLocker) {
        String expression = "execution(* " + EnableDistributedLockerRegistrar.PACKAGE_NAME + "..*.*(..))";
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        setPointcut(pointcut);
        setAdvice(new LockMethodInterceptor(distributedLocker));
    }
}

package com.fanxuankai.zeus.common.aop.util.configuration;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author fanxuankai
 */
public class LogPointcutAdvisor extends DefaultPointcutAdvisor {

    public LogPointcutAdvisor() {
        String expression = "execution(* " + EnableAopInterceptorRegistrar.PACKAGE_NAME + "..*.*(..))";
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        setPointcut(pointcut);
        setAdvice(new LogMethodInterceptor());
    }
}

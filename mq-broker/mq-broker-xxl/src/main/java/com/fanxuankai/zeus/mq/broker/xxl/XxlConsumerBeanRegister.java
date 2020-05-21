package com.fanxuankai.zeus.mq.broker.xxl;

import com.fanxuankai.zeus.mq.broker.config.EnableMqBrokerAttributes;
import com.fanxuankai.zeus.mq.broker.core.EventListener;
import com.fanxuankai.zeus.mq.broker.core.Listener;
import com.google.common.base.CaseFormat;
import com.xxl.mq.client.consumer.IMqConsumer;
import com.xxl.mq.client.consumer.MqResult;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

/**
 * @author fanxuankai
 */
public class XxlConsumerBeanRegister {

    public static void registry(BeanDefinitionRegistry registry) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages(EnableMqBrokerAttributes.getBasePackage())
                        .setScanners(new TypeAnnotationsScanner())
                );
        Set<Class<?>> classes = r.getTypesAnnotatedWith(Listener.class, true);
        if (classes == null || classes.isEmpty()) {
            return;
        }
        classes.stream()
                .filter(EventListener.class::isAssignableFrom)
                .map(XxlConsumerBeanRegister::newProxyClass)
                .map(proxyClass -> {
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(proxyClass);
                    return new BeanDefinitionHolder(beanDefinition, proxyClass.getName());
                })
                .forEach(bh -> BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry));
    }

    private static Class<?> newProxyClass(Class<?> elClass) {
        try {
            ClassPool pool = ClassPool.getDefault();
            String event = elClass.getAnnotation(Listener.class).event();
            CtClass eventConsumerCtClass = pool.getCtClass(XxlEventConsumer.class.getName());
            CtClass clazz =
                    pool.makeClass("MqBrokerXxlMqJavassistProxyMqConsumer4" +
                                    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, elClass.getSimpleName()),
                            eventConsumerCtClass);
            clazz.addInterface(pool.getCtClass(IMqConsumer.class.getName()));
            ConstPool constPool = clazz.getClassFile().getConstPool();
            String src = "public %s consume(String s) {this.accept($1); return %s.SUCCESS; }";
            src = String.format(src, MqResult.class.getName(), MqResult.class.getName());
            CtMethod proxyMethod = CtMethod.make(src, clazz);
            Annotation classAnnotation = new Annotation(MqConsumer.class.getName(), constPool);
            classAnnotation.addMemberValue("topic", new StringMemberValue(event, constPool));
            AnnotationsAttribute classAttribute = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            classAttribute.addAnnotation(classAnnotation);
            clazz.getClassFile().addAttribute(classAttribute);
            clazz.addMethod(proxyMethod);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

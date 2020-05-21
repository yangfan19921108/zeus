package com.fanxuankai.zeus.mq.broker.rabbit;

import com.fanxuankai.zeus.mq.broker.EventListenerFactory;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.Listener;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author fanxuankai
 */
public class RabbitListenerBeanRegister {

    public static void registry(BeanDefinitionRegistry registry) {
        Reflections r =
                new Reflections(new ConfigurationBuilder()
                        .forPackages("")
                        .setScanners(new TypeAnnotationsScanner())
                );
        Set<Class<?>> classes = r.getTypesAnnotatedWith(Listener.class, true);
        if (classes == null || classes.isEmpty()) {
            return;
        }
        EventListenerFactory.getListeners()
                .stream()
                .map(o -> newProxyClass(o.event()))
                .map(proxyClass -> {
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(proxyClass);
                    return new BeanDefinitionHolder(beanDefinition, proxyClass.getName());
                })
                .forEach(bh -> BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry));
    }

    public static Class<?> newProxyClass(String queue) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass eventConsumerCtClass = pool.getCtClass(RabbitEventConsumer.class.getName());
            CtClass clazz =
                    pool.makeClass(ClassUtils.getPackageName(RabbitListenerBeanRegister.class) +
                                    ".MqBrokerRabbitMqJavassistProxyRabbitListener4" + StringUtils.capitalize(queue),
                            eventConsumerCtClass);
            ConstPool constPool = clazz.getClassFile().getConstPool();
            String src = "public void proxyMethod(%s e) {this.accept($1);}";
            src = String.format(src, Event.class.getName());
            CtMethod proxyMethod = CtMethod.make(src, clazz);
            Annotation methodAnnotation = new Annotation(RabbitListener.class.getName(), constPool);
            Annotation queueAnnotation = new Annotation(Queue.class.getName(), constPool);
            queueAnnotation.addMemberValue("value",
                    new StringMemberValue(queue, constPool));
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
            arrayMemberValue.setValue(new MemberValue[]{new AnnotationMemberValue(queueAnnotation,
                    constPool)});
            methodAnnotation.addMemberValue("queuesToDeclare", arrayMemberValue);
            AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            methodAttr.addAnnotation(methodAnnotation);
            proxyMethod.getMethodInfo().addAttribute(methodAttr);
            clazz.addMethod(proxyMethod);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package com.fanxuankai.zeus.mq.broker.xxl;

import com.fanxuankai.zeus.mq.broker.core.consume.EventListenerFactory;
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
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author fanxuankai
 */
public class XxlConsumerBeanRegister {

    public static void registry(BeanDefinitionRegistry registry) {
        EventListenerFactory.getListeners()
                .stream()
                .map(o -> newProxyClass(o.event()))
                .map(proxyClass -> {
                    BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(proxyClass);
                    return new BeanDefinitionHolder(beanDefinition, proxyClass.getName());
                })
                .forEach(bh -> BeanDefinitionReaderUtils.registerBeanDefinition(bh, registry));
    }

    private static Class<?> newProxyClass(String topic) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass mqConsumerCtClass = pool.getCtClass(XxlMqConsumer.class.getName());
            CtClass clazz =
                    pool.makeClass(ClassUtils.getPackageName(XxlConsumerBeanRegister.class) +
                                    ".MqBrokerXxlMqJavassistProxyMqConsumer4" + StringUtils.capitalize(topic),
                            mqConsumerCtClass);
            clazz.addInterface(pool.getCtClass(IMqConsumer.class.getName()));
            ConstPool constPool = clazz.getClassFile().getConstPool();
            String src = "public %s consume(String s) {this.accept($1); return %s.SUCCESS; }";
            src = String.format(src, MqResult.class.getName(), MqResult.class.getName());
            CtMethod proxyMethod = CtMethod.make(src, clazz);
            Annotation classAnnotation = new Annotation(MqConsumer.class.getName(), constPool);
            classAnnotation.addMemberValue("topic", new StringMemberValue(topic, constPool));
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

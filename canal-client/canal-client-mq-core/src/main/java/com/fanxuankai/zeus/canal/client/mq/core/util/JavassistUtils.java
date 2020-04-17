package com.fanxuankai.zeus.canal.client.mq.core.util;

import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@Slf4j
public class JavassistUtils {

    /**
     * 添加 MqConsumer 注入字段
     *
     * @param ctClass    目标类
     * @param mqConsumer MqConsumer 具体类型
     * @param name       属性名
     */
    @SuppressWarnings("rawtypes")
    public static void injectionMqConsumer(CtClass ctClass, Class<? extends MqConsumer> mqConsumer, String name) {
        try {
            CtClass mqConsumerCtClass = ClassPool.getDefault().get(mqConsumer.getName());
            CtField mqConsumerField = new CtField(mqConsumerCtClass, name, ctClass);
            mqConsumerField.setModifiers(Modifier.PRIVATE);
            ctClass.addField(mqConsumerField);
            ConstPool constPool = ctClass.getClassFile().getConstPool();
            AnnotationsAttribute attribute = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            attribute.addAnnotation(new Annotation(Resource.class.getName(), constPool));
            mqConsumerField.getFieldInfo().addAttribute(attribute);
        } catch (Exception e) {
            throw new RuntimeException("添加 MqConsumer 注入属性失败", e);
        }
    }
}

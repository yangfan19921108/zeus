package com.fanxuankai.zeus.canal.client.xxl.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.util.JavassistUtils;
import com.xxl.mq.client.consumer.IMqConsumer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
public class JavassistBeanGenerator implements MqConsumer<String> {

    @SuppressWarnings("rawtypes")
    public static Class<?> generateXxlMqConsumer(Class<? extends MqConsumer> mqConsumer, Class<?> domainClass,
                                                 String topic, CanalEntry.EventType eventType) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(mqConsumer.getName() + "JavassistProxyXxlMqConsumer" + eventType);
            clazz.addInterface(pool.getCtClass(IMqConsumer.class.getName()));
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute classAttribute = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            Annotation mqConsumerAnnotation =
                    new Annotation(com.xxl.mq.client.consumer.annotation.MqConsumer.class.getName(),
                            constPool);
            mqConsumerAnnotation.addMemberValue("topic",
                    new StringMemberValue(topic + CommonConstants.SEPARATOR + eventType, constPool));
            classAttribute.addAnnotation(mqConsumerAnnotation);
            classFile.addAttribute(classAttribute);
            String mqConsumerFieldName = StringUtils.uncapitalize(mqConsumer.getSimpleName());
            JavassistUtils.injectionMqConsumer(clazz, mqConsumer, mqConsumerFieldName);
            addConsumeMethod(clazz, domainClass, eventType, mqConsumerFieldName);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addConsumeMethod(CtClass clazz,
                                         Class<?> domainClass,
                                         CanalEntry.EventType eventType,
                                         String mqConsumerFieldName)
            throws CannotCompileException {
        if (eventType == CanalEntry.EventType.INSERT) {
            clazz.addMethod(CtMethod.make(insertSrc(domainClass, mqConsumerFieldName), clazz));
        } else if (eventType == CanalEntry.EventType.UPDATE) {
            clazz.addMethod(CtMethod.make(updateSrc(domainClass, mqConsumerFieldName), clazz));
        } else if (eventType == CanalEntry.EventType.DELETE) {
            clazz.addMethod(CtMethod.make(deleteSrc(domainClass, mqConsumerFieldName), clazz));
        }
    }

    private static String insertSrc(Class<?> domainClass, String mqConsumerFieldName) {
        String src = "public com.xxl.mq.client.consumer.MqResult consume(String s) {\n" +
                "    %s t = com.fanxuankai.zeus.canal.client.core.util.DomainConverter.of($1, %s.class);\n" +
                "    this.%s.insert(t);\n" +
                "    return com.xxl.mq.client.consumer.MqResult.SUCCESS;\n" +
                "}";
        return String.format(src, domainClass.getName(), domainClass.getName(), mqConsumerFieldName);
    }

    private static String updateSrc(Class<?> domainClass, String mqConsumerFieldName) {
        String src = "public com.xxl.mq.client.consumer.MqResult consume(String s) {\n" +
                "    org.apache.commons.lang3.tuple.Pair p = com.fanxuankai.zeus.canal.client.core.util" +
                ".DomainConverter.pairOf($1, %s.class);\n" +
                "    this.%s.update(p.getLeft(), p.getRight());\n" +
                "    return com.xxl.mq.client.consumer.MqResult.SUCCESS;\n" +
                "}";
        return String.format(src, domainClass.getName(), mqConsumerFieldName);
    }

    private static String deleteSrc(Class<?> domainClass, String mqConsumerFieldName) {
        String src = "public com.xxl.mq.client.consumer.MqResult consume(String s) {\n" +
                "    %s t = com.fanxuankai.zeus.canal.client.core.util.DomainConverter.of($1, %s.class);\n" +
                "    this.%s.delete(t);\n" +
                "    return com.xxl.mq.client.consumer.MqResult.SUCCESS;\n" +
                "}";
        return String.format(src, domainClass.getName(), domainClass.getName(), mqConsumerFieldName);
    }

    private static void w(byte[] code, String name) throws Exception {
        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream(name + ".class");
        fos.write(code);
        fos.close();
    }
}

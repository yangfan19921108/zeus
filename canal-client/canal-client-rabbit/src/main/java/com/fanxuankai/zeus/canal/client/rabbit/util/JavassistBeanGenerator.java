package com.fanxuankai.zeus.canal.client.rabbit.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.util.DomainConverter;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.canal.client.mq.core.util.JavassistUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
public class JavassistBeanGenerator {

    @SuppressWarnings("rawtypes")
    public static Class<?> generateRabbitMqConsumer(Class<? extends MqConsumer> mqConsumer, Class<?> domainType,
                                                    String topic) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(mqConsumer.getName() + "JavassistProxyRabbitMqConsumer");
            String fieldName = StringUtils.uncapitalize(mqConsumer.getSimpleName());
            JavassistUtils.injectionMqConsumer(clazz, mqConsumer, fieldName);
            addInsertMethod(clazz, fieldName, domainType, topic);
            addUpdateMethod(clazz, fieldName, domainType, topic);
            addDeleteMethod(clazz, fieldName, domainType, topic);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addInsertMethod(CtClass clazz, String mqConsumerFieldName, Class<?> domainType, String topic)
            throws CannotCompileException {
        String src = "public void insert(String s) {this.%s.insert(%s.of($1, %s.class));}";
        src = String.format(src, mqConsumerFieldName, DomainConverter.class.getName(),
                domainType.getName());
        CtMethod insertMethod = CtMethod.make(src, clazz);
        insertMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(clazz.getClassFile().getConstPool(),
                topic, CanalEntry.EventType.INSERT));
        clazz.addMethod(insertMethod);
    }

    private static void addUpdateMethod(CtClass clazz, String mqConsumerFieldName, Class<?> domainType, String topic)
            throws CannotCompileException {
        String src = "public void update(String s) {\n" +
                "    %s p = %s.pairOf($1, %s.class);\n" +
                "    this.%s.update(p.getLeft(), p.getRight());\n" +
                "}";
        src = String.format(src, Pair.class.getName(), DomainConverter.class.getName(), domainType.getName(),
                mqConsumerFieldName);
        CtMethod updateMethod = CtMethod.make(src, clazz);
        updateMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(clazz.getClassFile().getConstPool(),
                topic, CanalEntry.EventType.UPDATE));
        clazz.addMethod(updateMethod);
    }

    private static void addDeleteMethod(CtClass clazz, String mqConsumerFieldName, Class<?> domainClass, String topic)
            throws CannotCompileException {
        String src = "public void delete(String s) { this.%s.delete(%s.of($1, %s.class));}";
        CtMethod deleteMethod = CtMethod.make(String.format(src, mqConsumerFieldName,
                DomainConverter.class.getName(), domainClass.getName()), clazz);
        deleteMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(clazz.getClassFile().getConstPool(),
                topic, CanalEntry.EventType.DELETE));
        clazz.addMethod(deleteMethod);
    }

    private static AttributeInfo rabbitMqMethodAttributeInfo(ConstPool constPool, String topic,
                                                             CanalEntry.EventType eventType) {
        Annotation rlAnnotation = new Annotation(RabbitListener.class.getName(), constPool);
        Annotation queueAnnotation = new Annotation(Queue.class.getName(), constPool);
        queueAnnotation.addMemberValue("value",
                new StringMemberValue(topic + CommonConstants.SEPARATOR + eventType, constPool));
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
        arrayMemberValue.setValue(new MemberValue[]{new AnnotationMemberValue(queueAnnotation, constPool)});
        rlAnnotation.addMemberValue("queuesToDeclare", arrayMemberValue);
        //方法附上注解
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        methodAttr.addAnnotation(rlAnnotation);
        return methodAttr;
    }

    private static void w(byte[] code, String name) throws Exception {
        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream(name + ".class");
        fos.write(code);
        fos.close();
    }
}

package com.fanxuankai.zeus.canal.client.rabbit.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.util.DomainConverter;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.asm.Opcodes;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
public class JavassistBeanGenerator extends ClassLoader implements Opcodes {

    public static Class<?> generateRabbitMqConsumer(Class<?> domainType, String topic) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(domainType.getName() + "JavassistProxyRabbitMqConsumer");
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute classAttribute = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            // 类注解
            classAttribute.addAnnotation(new Annotation(Service.class.getName(), constPool));
            classFile.addAttribute(classAttribute);
            addConsumerConstructor(clazz);

            String domainConverterName = DomainConverter.class.getName();
            // insert 方法
            String insertSrcFormat = "public void insert(String s) {" +
                    "%s t = %s.of($1, " +
                    "%s.class);" +
                    "this.mqConsumer.insert(t);" +
                    "}";
            CtMethod insertMethod = CtMethod.make(String.format(insertSrcFormat, domainType.getName(),
                    domainConverterName, domainType.getName()),
                    clazz);
            insertMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(constPool, topic,
                    CanalEntry.EventType.INSERT));
            clazz.addMethod(insertMethod);
            // update 方法
            String updateSrcFormat = "public void update(String s) {" +
                    "org.apache.commons.lang3.tuple.Pair p = %s.pairOf" +
                    "($1, " +
                    "%s.class);" +
                    "this.mqConsumer.update(p.getLeft(), p.getRight());" +
                    "}";
            CtMethod updateMethod = CtMethod.make(String.format(updateSrcFormat, domainConverterName,
                    domainType.getName()), clazz);
            updateMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(constPool, topic,
                    CanalEntry.EventType.UPDATE));
            clazz.addMethod(updateMethod);
            // delete 方法
            String deleteSrcFormat = "public void delete(String s) {" +
                    "%s t = %s.of($1, " +
                    "%s.class);" +
                    "this.mqConsumer.delete(t);" +
                    "}";
            CtMethod deleteMethod = CtMethod.make(String.format(deleteSrcFormat, domainType.getName(),
                    domainConverterName, domainType.getName()),
                    clazz);
            deleteMethod.getMethodInfo().addAttribute(rabbitMqMethodAttributeInfo(constPool, topic,
                    CanalEntry.EventType.DELETE));
            clazz.addMethod(deleteMethod);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addConsumerConstructor(CtClass clazz) throws NotFoundException, CannotCompileException {
        CtClass mqConsumerCtClass = ClassPool.getDefault().get(MqConsumer.class.getName());

        // 增加字段
        CtField mqConsumerField = new CtField(mqConsumerCtClass, "mqConsumer", clazz);
        mqConsumerField.setModifiers(Modifier.PRIVATE);
        clazz.addField(mqConsumerField);

        //添加构造函数
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{mqConsumerCtClass}, clazz);
        //为构造函数设置函数体
        ctConstructor.setBody("{this.mqConsumer=$1;}");
        //把构造函数添加到新的类中
        clazz.addConstructor(ctConstructor);
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

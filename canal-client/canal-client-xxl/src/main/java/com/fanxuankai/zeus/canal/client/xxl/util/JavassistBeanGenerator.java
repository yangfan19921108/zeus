package com.fanxuankai.zeus.canal.client.xxl.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.util.DomainConverter;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.xxl.mq.client.consumer.IMqConsumer;
import com.xxl.mq.client.consumer.MqResult;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.asm.Opcodes;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
public class JavassistBeanGenerator extends ClassLoader implements Opcodes {

    public static Class<?> generateXxlMqConsumer(Class<?> domainType, String topic, CanalEntry.EventType eventType) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(domainType.getName() + "JavassistProxyXxlMqConsumer" + eventType);
            clazz.addInterface(pool.getCtClass(IMqConsumer.class.getName()));
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute classAttribute = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
            // 类注解
            classAttribute.addAnnotation(new Annotation(Service.class.getName(), constPool));
            Annotation mqConsumerAnnotation =
                    new Annotation(com.xxl.mq.client.consumer.annotation.MqConsumer.class.getName(),
                            constPool);
            mqConsumerAnnotation.addMemberValue("topic",
                    new StringMemberValue(topic + CommonConstants.SEPARATOR + eventType, constPool));
            classAttribute.addAnnotation(mqConsumerAnnotation);
            classFile.addAttribute(classAttribute);
            addConsumerConstructor(clazz);
            clazz.addMethod(CtMethod.make(xxlConsumeMethodSrc(domainType, eventType), clazz));
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String xxlConsumeMethodSrc(Class<?> type, CanalEntry.EventType eventType) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(MqResult.class.getName()).append(" consume(String s) {");
        if (eventType == CanalEntry.EventType.INSERT) {
            sb.append(type.getName()).append(" t ").append(" = ").append(DomainConverter.class.getName()).append(".of" +
                    "($1, ").append(type.getName()).append(".class);");
            sb.append("this.mqConsumer.insert(t);");
        } else if (eventType == CanalEntry.EventType.UPDATE) {
            sb.append(Pair.class.getName()).append(" p ").append(" = ").append(DomainConverter.class.getName()).append(".pairOf" +
                    "($1, ").append(type.getName()).append(".class);");
            sb.append("this.mqConsumer.update(p.getLeft(), p.getRight());");
        } else if (eventType == CanalEntry.EventType.DELETE) {
            sb.append(type.getName()).append(" t ").append(" = ").append(DomainConverter.class.getName()).append(".of" +
                    "($1, ").append(type.getName()).append(".class);");
            sb.append("this.mqConsumer.delete(t);");
        }
        sb.append("return ");
        sb.append(MqResult.class.getName());
        sb.append(".SUCCESS;");
        sb.append("}");
        return sb.toString();
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

    private static void w(byte[] code, String name) throws Exception {
        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream(name + ".class");
        fos.write(code);
        fos.close();
    }
}

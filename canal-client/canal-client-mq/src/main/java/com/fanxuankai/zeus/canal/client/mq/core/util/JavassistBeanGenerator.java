package com.fanxuankai.zeus.canal.client.mq.core.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fanxuankai.zeus.canal.client.core.constants.CommonConstants;
import com.fanxuankai.zeus.canal.client.core.util.DomainConverter;
import com.fanxuankai.zeus.canal.client.mq.core.consumer.MqConsumer;
import com.fanxuankai.zeus.mq.broker.core.Event;
import com.fanxuankai.zeus.mq.broker.core.EventRegistry;
import com.fanxuankai.zeus.mq.broker.core.consume.EventListener;
import com.fanxuankai.zeus.mq.broker.core.consume.Listener;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
@SuppressWarnings("rawtypes")
public class JavassistBeanGenerator {

    public static Class<?>[] generateRabbitMqConsumer(Class<? extends MqConsumer> mqConsumer, Class<?> domainType,
                                                      String topic) {
        try {
            String fieldName = StringUtils.uncapitalize(mqConsumer.getSimpleName());
            CtClass insertClazz = clazz(mqConsumer, topic, CanalEntry.EventType.INSERT, fieldName);
            addMethodForInsert(insertClazz, fieldName, domainType);
            CtClass updateClazz = clazz(mqConsumer, topic, CanalEntry.EventType.UPDATE, fieldName);
            addMethodForUpdate(updateClazz, fieldName, domainType);
            CtClass deleteClazz = clazz(mqConsumer, topic, CanalEntry.EventType.DELETE, fieldName);
            addMethodForDelete(deleteClazz, fieldName, domainType);
            return new Class<?>[]{insertClazz.toClass(), updateClazz.toClass(), deleteClazz.toClass()};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CtClass clazz(Class<? extends MqConsumer> mqConsumer, String topic, CanalEntry.EventType eventType
            , String fieldName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.makeClass(mqConsumer.getName() + "JavassistProxyEventListener" + eventType);
        clazz.addInterface(pool.getCtClass(EventListener.class.getName()));
        JavassistUtils.injectionMqConsumer(clazz, mqConsumer, fieldName);
        ClassFile classFile = clazz.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute classAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation mqConsumerAnnotation = new Annotation(Listener.class.getName(), constPool);
        String event = topic + CommonConstants.SEPARATOR + eventType;
        EventRegistry.register(event);
        mqConsumerAnnotation.addMemberValue("event", new StringMemberValue(event, constPool));
        classAttribute.addAnnotation(mqConsumerAnnotation);
        classFile.addAttribute(classAttribute);
        return clazz;
    }

    private static void addMethodForInsert(CtClass clazz, String mqConsumerFieldName, Class<?> domainType)
            throws CannotCompileException {
        String src = "public void onEvent(%s s) {this.%s.insert(%s.of($1.getData(), %s.class));}";
        src = String.format(src, Event.class.getName(), mqConsumerFieldName, DomainConverter.class.getName(),
                domainType.getName());
        CtMethod insertMethod = CtMethod.make(src, clazz);
        clazz.addMethod(insertMethod);
    }

    private static void addMethodForUpdate(CtClass clazz, String mqConsumerFieldName, Class<?> domainType)
            throws CannotCompileException {
        String src = "public void onEvent(%s s) {\n" +
                "    %s p = %s.pairOf($1.getData(), %s.class);\n" +
                "    this.%s.update(p.getLeft(), p.getRight());\n" +
                "}";
        src = String.format(src, Event.class.getName(), Pair.class.getName(), DomainConverter.class.getName(),
                domainType.getName(),
                mqConsumerFieldName);
        CtMethod updateMethod = CtMethod.make(src, clazz);
        clazz.addMethod(updateMethod);
    }

    private static void addMethodForDelete(CtClass clazz, String mqConsumerFieldName, Class<?> domainClass)
            throws CannotCompileException {
        String src = "public void onEvent(%s s) { this.%s.delete(%s.of($1.getData(), %s.class));}";
        CtMethod deleteMethod = CtMethod.make(String.format(src, Event.class.getName(), mqConsumerFieldName,
                DomainConverter.class.getName(), domainClass.getName()), clazz);
        clazz.addMethod(deleteMethod);
    }

    private static void w(byte[] code, String name) throws Exception {
        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream(name + ".class");
        fos.write(code);
        fos.close();
    }
}

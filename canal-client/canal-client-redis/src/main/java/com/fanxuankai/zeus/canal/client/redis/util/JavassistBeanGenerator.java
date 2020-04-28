package com.fanxuankai.zeus.canal.client.redis.util;

import com.fanxuankai.zeus.canal.client.redis.repository.SimpleRedisRepository;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import java.io.FileOutputStream;

/**
 * Javassist 工具类
 *
 * @author fanxuankai
 */
public class JavassistBeanGenerator {

    public static Class<?> generateRedisRepository(Class<?> redisRepositoryInterface, Class<?> domainType) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(redisRepositoryInterface.getName() +
                            "JavassistProxyImpl",
                    pool.getCtClass(SimpleRedisRepository.class.getName()));
            clazz.addInterface(pool.getCtClass(redisRepositoryInterface.getName()));
            //添加构造函数
            CtConstructor ctConstructor = new CtConstructor(null, clazz);
            //为构造函数设置函数体
            ctConstructor.setBody(String.format("{setDomainType(%s.class);}", domainType.getName()));
            //把构造函数添加到新的类中
            clazz.addConstructor(ctConstructor);
            return clazz.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void w(byte[] code, String name) throws Exception {
        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream(name + ".class");
        fos.write(code);
        fos.close();
    }
}

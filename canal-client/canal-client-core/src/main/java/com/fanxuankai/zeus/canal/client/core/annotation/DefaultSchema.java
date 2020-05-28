package com.fanxuankai.zeus.canal.client.core.annotation;

import com.fanxuankai.zeus.canal.client.core.config.DefaultSchemaImportRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fanxuankai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DefaultSchemaImportRegistrar.class})
public @interface DefaultSchema {

    /**
     * 获取数据库名称的步骤, 先后顺序如下:<p>
     * CanalTable.schema()<p>
     * javax.persistence.Table.schema()<p>
     * EnableCanal.schema()<p>
     * 若始终无法获取到数据库名称, 程序终止, 抛出异常
     *
     * @return 默认的数据库名称
     */
    String value() default "";

}

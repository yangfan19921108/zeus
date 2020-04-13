package com.fanxuankai.zeus.canal.client.core.metadata;

import com.fanxuankai.zeus.canal.client.core.annotation.CanalTable;
import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author fanxuankai
 */
@AllArgsConstructor
@Getter
public class CanalTableMetadata {
    private final String schema;
    private final String name;
    private final Class<?> domainType;

    public CanalTableMetadata(CanalTable canalTable, Class<?> domainType) {
        String schema = Optional.ofNullable(canalTable).map(CanalTable::schema).orElse("");
        Optional<TableAttributes> optionalTableAttributes = TableAttributes.from(domainType);
        if (StringUtils.isBlank(schema)) {
            schema = optionalTableAttributes.map(TableAttributes::getSchema).orElse("");
            if (StringUtils.isBlank(schema)) {
                schema = EnableCanalAttributes.getDefaultSchema();
                if (StringUtils.isBlank(schema)) {
                    throw new RuntimeException(String.format("无法找到 %s 所对应的数据库名", domainType.getName()));
                }
            }
        }
        String name = Optional.ofNullable(canalTable).map(CanalTable::name).orElse("");
        if (StringUtils.isBlank(name)) {
            name = optionalTableAttributes.map(TableAttributes::getName).orElse("");
            if (StringUtils.isBlank(name)) {
                name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, domainType.getSimpleName());
            }
        }

        this.schema = schema;
        this.name = name;
        this.domainType = domainType;
    }
}

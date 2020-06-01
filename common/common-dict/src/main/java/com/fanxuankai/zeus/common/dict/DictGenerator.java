package com.fanxuankai.zeus.common.dict;

import com.fanxuankai.zeus.common.dict.domain.SysDict;
import com.fanxuankai.zeus.common.dict.domain.SysDictType;
import com.fanxuankai.zeus.common.dict.service.SysDictService;
import com.fanxuankai.zeus.common.dict.service.SysDictTypeService;
import com.fanxuankai.zeus.common.dict.vo.SysDictVO;
import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fanxuankai
 */
@Service
@Slf4j
public class DictGenerator {
    @Resource
    private SysDictTypeService sysDictTypeService;
    @Resource
    private SysDictService sysDictService;

    public void generate(GenerateModel generateModel) {
        List<SysDictType> dictTypes = sysDictTypeService.list();
        if (dictTypes.isEmpty()) {
            return;
        }
        Map<Long, List<SysDict>> map =
                sysDictService.map(dictTypes.stream().map(SysDictType::getId).collect(Collectors.toList()));
        List<SysDictVO> dictVOList = dictTypes.stream()
                .filter(sysDictType -> map.get(sysDictType.getId()) != null)
                .sorted(Comparator.comparing(SysDictType::getId))
                .map(sysDictType -> {
                    List<SysDict> dictList = map.get(sysDictType.getId());
                    dictList.sort(Comparator.comparing(SysDict::getSort));
                    dictList.forEach(sysDict ->
                            sysDict.setEnglishName(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,
                                    sysDict.getEnglishName())));
                    return new SysDictVO()
                            .setTypeName(StringUtils.capitalize(sysDictType.getName()))
                            .setTypeDescription(sysDictType.getDescription())
                            .setDictList(dictList);
                }).collect(Collectors.toList());
        DictEnumsModel model = new DictEnumsModel()
                .setPackageName(ClassUtils.getPackageName(generateModel.getClassName()))
                .setShortName(ClassUtils.getShortName(generateModel.getClassName()))
                .setAuth(generateModel.getAuth())
                .setDictVOList(dictVOList);
        Configuration cfg = new Configuration(Configuration.getVersion());
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        try {
            Template template = cfg.getTemplate("dict.ftl");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
                    generateModel.getPath() + "/"
                            + ClassUtils.convertClassNameToResourcePath(generateModel.getClassName())
                            + ".java"))));
            template.process(model, writer);
            log.info("生成成功");
            writer.close();
        } catch (IOException | TemplateException e) {
            log.error("生成失败", e);
        }
    }
}

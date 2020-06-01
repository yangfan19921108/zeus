package com.fanxuankai.zeus.common.dict.example;

import com.alibaba.fastjson.JSON;
import com.fanxuankai.zeus.common.dict.DictGenerator;
import com.fanxuankai.zeus.common.dict.GenerateModel;
import com.fanxuankai.zeus.common.dict.domain.SysDict;
import com.fanxuankai.zeus.common.dict.domain.SysDictType;
import com.fanxuankai.zeus.common.dict.service.SysDictService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanxuankai
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysDictTest {
    @Resource
    private SysDictService sysDictService;
    @Resource
    private DictGenerator dictGenerator;

    @Test
    public void delete() {
        sysDictService.delete("deleted");
        sysDictService.delete("colour");
    }

    @Test
    public void addAll() {
        List<SysDict> list = new ArrayList<>(3);
        list.add(new SysDict().setEnglishName("white").setChineseName("白色"));
        list.add(new SysDict().setEnglishName("red").setChineseName("红色"));
        list.add(new SysDict().setEnglishName("black").setChineseName("黑色"));
        sysDictService.addAll(new SysDictType().setName("colour").setDescription("颜色"), list, true);
        list = new ArrayList<>(2);
        list.add(new SysDict().setEnglishName("no").setChineseName("未删除"));
        list.add(new SysDict().setEnglishName("yes").setChineseName("已删除"));
        sysDictService.addAll(new SysDictType().setName("deleted").setDescription("是否删除"), list, true);
    }

    @Test
    public void list() {
        System.out.println(JSON.toJSONString(sysDictService.list("deleted"), true));
    }

    @Test
    public void generate() {
        dictGenerator.generate(new GenerateModel()
                .setAuth("fanxuankai")
                .setPath("/Users/fanxuankai/Java/Workspace/myproject/fanxuankai/zeus" +
                        "/example/common-dict-example/src/test/java")
                .setClassName("com.fanxuankai.zeus.common.dict.example.Dict"));
    }
}

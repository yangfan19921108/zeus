package com.fanxuankai.zeus.spring.cloud.admin.web;

import com.fanxuankai.zeus.spring.cloud.admin.config.AdminProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fanxuankai
 */
@RestController
@RequestMapping("users")
public class UserController {

    @Resource
    private AdminProperties adminProperties;

    @GetMapping
    public String get() {
        return adminProperties.getUsername();
    }
}

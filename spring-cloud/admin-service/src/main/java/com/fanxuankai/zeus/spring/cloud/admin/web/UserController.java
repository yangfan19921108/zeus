package com.fanxuankai.zeus.spring.cloud.admin.web;

import com.fanxuankai.zeus.aop.util.annotation.Log;
import com.fanxuankai.zeus.spring.cloud.admin.config.AdminProperties;
import com.fanxuankai.zeus.util.concurrent.Threads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author fanxuankai
 */
@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    @Resource
    private AdminProperties adminProperties;

    @GetMapping("mono")
    @Log
    public Mono<String> getMono() {
        return Mono.fromSupplier(this::getUsername);
    }

    @GetMapping()
    @Log
    public String get() {
        return getUsername();
    }

    private String getUsername() {
        Threads.sleep(5, TimeUnit.SECONDS);
        return adminProperties.getUsername();
    }

}

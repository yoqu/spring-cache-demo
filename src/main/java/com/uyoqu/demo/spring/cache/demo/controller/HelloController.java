package com.uyoqu.demo.spring.cache.demo.controller;

import com.uyoqu.demo.spring.cache.demo.model.User;
import com.uyoqu.demo.spring.cache.demo.service.IUserSvc;
import com.uyoqu.hello.docs.core.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
@RestController
@RequestMapping("/")
public class HelloController {

    @Autowired
    IUserSvc userSvc;


    @GetMapping("/")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiServiceDocs(cnName = "首页", group = "缓存模块", desc = "首页获取用户列表", finish = 100, version = "1.0")
    @ApiOut(@Out(param = "List<User>", link = "User", desc = "用户列表", type = "User"))
    public List<User> index() {
        return userSvc.findAll();
    }

    @GetMapping("/{id}")
    @ApiServiceDocs(cnName = "用户详情", group = "缓存模块", desc = "用户详情", finish = 100, version = "1.0")
    public User detail(@In(remark = "这是一个备注", desc = "主键id") @PathVariable("id") String id) {
        return userSvc.findById(id);
    }

    @GetMapping("test")
    @ApiServiceDocs(cnName = "测试", group = "缓存模块", desc = "test", finish = 50, version = "1.0")
    @ApiOut(@Out(param = "List<User>", link = "User", desc = "用户列表", type = "User"))
    @ApiTimeline(@Timeline(time = "2018-10-18", content = "first test"))
    public List<User> test(User user) {
        return Collections.emptyList();
    }
}

package com.uyoqu.demo.spring.cache.demo.controller;

import com.uyoqu.demo.spring.cache.demo.model.User;
import com.uyoqu.demo.spring.cache.demo.service.IUserSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<User> index() {
        return userSvc.findAll();
    }

    @GetMapping("/{id}")
    public User detail(@PathVariable("id") String id) {
        return userSvc.findById(id);
    }
}

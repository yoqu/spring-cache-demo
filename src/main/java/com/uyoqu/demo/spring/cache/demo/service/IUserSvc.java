package com.uyoqu.demo.spring.cache.demo.service;

import com.uyoqu.demo.spring.cache.demo.model.User;

import java.util.List;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
public interface IUserSvc {

    public List<User> findAll();

    public User findById(String id);

}

package com.uyoqu.demo.spring.cache.demo.service;

import com.uyoqu.demo.spring.cache.demo.model.User;
import com.uyoqu.demo.spring.cache.demo.service.handler.UserListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
@Service
public class UserSvcImpl implements IUserSvc {

    @Autowired
    UserListHandler userListHandler;

    @Override
    public List<User> findAll() {
        return userListHandler.findAll();
    }

    @Override
    public User findById(String id) {
        return userListHandler.findById(id);
    }
}

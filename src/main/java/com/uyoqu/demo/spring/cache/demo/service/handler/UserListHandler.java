package com.uyoqu.demo.spring.cache.demo.service.handler;

import com.uyoqu.demo.spring.cache.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
@Service
public class UserListHandler {

    public static final Logger logger = LoggerFactory.getLogger(UserListHandler.class);

    @Cacheable(value = "dbusers")
    public List<User> findAll() {
        logger.info(">>>>>>>>>>>>>>查找全部用户<<<<<<<<<<<<<<<<<");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setAge(100);
        user.setName("张三");
        user.setArea("北京");
        user.setId("1");
        User user1 = new User();
        user1.setAge(20);
        user1.setName("李四");
        user1.setArea("成都");
        user1.setId("2");
        users.add(user);
        users.add(user1);
        return users;
    }

    @Cacheable(value = "userdetail", key = "#id")
    public User findById(String id) {
        logger.info(">>>>>>>>>>>>>>查找单个用户<<<<<<<<<<<<<<<<<");
        User user = new User();
        user.setAge(100);
        user.setName("张三");
        user.setArea("北京");
        user.setId("1");
        return user;
    }
}

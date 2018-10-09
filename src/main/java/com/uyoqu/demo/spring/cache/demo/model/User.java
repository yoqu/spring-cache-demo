package com.uyoqu.demo.spring.cache.demo.model;

import java.io.Serializable;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
public class User implements Serializable {
    private String name;
    private String id;
    private int age;
    private String area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}

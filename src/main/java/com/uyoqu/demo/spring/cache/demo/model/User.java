package com.uyoqu.demo.spring.cache.demo.model;

import com.uyoqu.hello.docs.core.annotation.ApiBasicFiled;
import com.uyoqu.hello.docs.core.annotation.ApiDTO;

import java.io.Serializable;

/**
 * @author: yoqu
 * @date: 2018/10/8
 * @email: yoqulin@qq.com
 **/
@ApiDTO(cnName = "用户", enName = "User", desc = "用户信息")
public class User implements Serializable {

    @ApiBasicFiled(desc = "名字")
    private String name;

    @ApiBasicFiled(desc = "id")
    private String id;

    @ApiBasicFiled(desc = "年龄")
    private int age;

    @ApiBasicFiled(desc = "区域")
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

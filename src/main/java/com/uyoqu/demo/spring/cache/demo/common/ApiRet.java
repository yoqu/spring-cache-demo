package com.uyoqu.demo.spring.cache.demo.common;

import java.io.Serializable;

/**
 * @author: yoqu
 * @date: 2018/10/18
 * @email: yoqulin@qq.com
 **/
public class ApiRet implements Serializable {
    private String code;
    private String msg;

    public ApiRet(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
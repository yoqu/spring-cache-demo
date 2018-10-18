package com.uyoqu.demo.spring.cache.demo.common;

import com.uyoqu.hello.docs.core.annotation.ApiGlobalCode;

/**
 * @author: yoqu
 * @date: 2018/10/18
 * @email: yoqulin@qq.com
 **/
@ApiGlobalCode(codeKey  ="code",msgKey = "msg")
public class ApiRetCodes {
    /**
     * 00xx - 成功
     */
    public static final ApiRet SUCCESS = new ApiRet("0000", "成功");

    /**
     * 9xxx - 异常
     */
    public static final ApiRet FAIL = new ApiRet("9999", "系统异常");
}

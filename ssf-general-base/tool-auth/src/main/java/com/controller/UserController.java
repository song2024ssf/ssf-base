package com.controller;

import com.ssf.common.annotation.CurrentUser;
import com.ssf.common.annotation.IgnoreAuth;
import com.ssf.common.exception.BusinessException;
import com.ssf.common.model.LoginUser;
import com.ssf.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    // 受保护接口：需要 Token
    @GetMapping("/profile")
    public Result<LoginUser> profile(@CurrentUser LoginUser user) {
        return Result.success(user);
    }

    // 测试 @IgnoreAuth：跳过鉴权
    @IgnoreAuth
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("This is a public endpoint, no token required.");
    }

    // 测试异常处理
    @GetMapping("/test-exception")
    public Result<String> testException(@RequestParam(required = false) String trigger) {
        if ("error".equals(trigger)) {
            throw new BusinessException(1001, "手动触发的业务异常");
        }
        return Result.success("正常响应");
    }
}
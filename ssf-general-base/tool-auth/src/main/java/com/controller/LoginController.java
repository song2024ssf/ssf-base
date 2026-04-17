package com.controller;

import com.ssf.common.annotation.IgnoreAuth;
import com.ssf.common.model.LoginUser;
import com.ssf.common.response.Result;
import com.ssf.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final JwtUtils jwtUtils;

    @IgnoreAuth
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // 模拟登录校验（实际应查数据库）
        if (!"admin".equals(username) || !"123456".equals(password)) {
            return Result.error(401, "用户名或密码错误");
        }

        LoginUser user = new LoginUser();
        user.setUserId(1L);
        user.setUsername(username);

        String token = jwtUtils.generateToken(user);
        return Result.success(Map.of("token", token));
    }
}
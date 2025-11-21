package com.ny.controller;

import com.ny.entity.User;
import com.ny.service.UserService;
import com.ny.until.JwtUntil;
import com.ny.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUntil jwtUntil;

    private final Map<String, Object> map = new HashMap<>();

    // 登录接口：POST /login
    //http://localhost:8081/login?username=admin&password=<PASSWORD>
    @PostMapping("/login")
    public Result login(String username, String password) {
        User user = userService.findUser(username, password);
        if (user != null) {
            String token = jwtUntil.generateToken(user.getUsername());// 生成Token
            jwtUntil.storeToken(token, user.getUsername());// 存储到Redis
            map.put("token", token);// Token存入返回结果
            map.put("users", user);// 用户信息存入返回结果
            return Result.success(map);// 返回成功结果
        } else {
            return Result.error("用户名和密码错误");// 返回错误信息
        }
    }
}
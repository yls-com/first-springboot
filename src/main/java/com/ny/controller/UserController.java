package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.User;
import com.ny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/selectAllUser")
    public Result selectAllUser(){
        return Result.success(userService.findAllUser());
    }

    // 登录接口
    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户名或密码错误");
        }
    }
}


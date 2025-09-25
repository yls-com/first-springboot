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

    // 根据用户名查询用户信息 http://localhost:8080/findUserByUsername?username=admin
    @GetMapping("/findUserByUsername")
    public Result findUserByUsername(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        System.out.println(user);
        if (username != null){
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    // 注册接口 http://localhost:8080/register
    @PostMapping("/register")
    public Result register(User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

}


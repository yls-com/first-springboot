package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.User;
import com.ny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 查询所有用户接口 http://localhost:8082/selectAllUser
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

    // 根据用户名查询用户信息 http://localhost:8082/findUserByUsername?username=admin
    @GetMapping("/findUserByUsername")
    public Result findUserByUsername(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        System.out.println(user);
        if (username != null){
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    // 注册接口 http://localhost:8082/register
    @PostMapping("/register")
    public Result register(User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    // 根据邮箱查询用户信息 http://localhost:8082/findUserByEmail?email=<EMAIL>
    @GetMapping("/findUserByEmail")
    public Result findUserByEmail(String email){
        User user = userService.findUserByEmail(email);
        if (user != null){
            return Result.success(user);
        }
        return Result.error("邮箱不存在");
    }
    // 发送邮箱验证码 http://localhost:8082/sendEmailCode?email=<EMAIL>
    @GetMapping("/sendEmailCode")
    public Result sendEmailCode(String email){
        User user = userService.findUserByEmail(email);
        if (user != null){
            userService.sendEmailCode(email);
            return Result.success("验证码发送成功");
        }
        return Result.notFound("邮箱不存在");
    }
    // 修改密码 http://localhost:8082/updatePasswordByEmail?email=<EMAIL>&password=<PASSWORD>&code=
    @PutMapping("/updatePasswordByEmail")
    public Result updatePasswordByEmail(String email, String password,String code){
        Boolean checkCode =userService.checkCode(email, code);
        if (checkCode){
            int i =userService.updatePasswordByEmail(email, password);
            if (i>0){
                return Result.success("修改成功");
            }
            return Result.notFound("成功获取验证码");
        }
        else {
            return Result.success("验证码错误");
        }

    }
}


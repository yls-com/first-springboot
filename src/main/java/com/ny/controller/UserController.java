package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.User;
import com.ny.service.UserService;
import com.ny.until.JwtUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUntil jwtUntil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final Map<String, Object> map = new HashMap<>();

    // 查询所有用户接口 http://localhost:8081/selectAllUser
    @GetMapping("/selectAllUser")
    public Result selectAllUser(){
        return Result.success(userService.findAllUser());
    }

    // 登录接口：POST /login
    // 支持两种方式：
    // 1. JSON请求体: POST http://localhost:8081/login
    //    Body: {"username": "admin", "password": "123456"}
    // 2. 表单参数: POST http://localhost:8081/login?username=admin&password=123456
    @PostMapping("/login")
    public Result login(@RequestBody(required = false) Map<String, String> credentials,
                        @RequestParam(required = false) String username,
                        @RequestParam(required = false) String password) {
        // 判断使用哪种方式传递参数
        String uname, pwd;
        if (credentials != null && !credentials.isEmpty()) {
            // JSON请求体方式
            uname = credentials.get("username");
            pwd = credentials.get("password");
        } else {
            // 查询参数方式
            uname = username;
            pwd = password;
        }
        
        // 检查用户名和密码是否为空
        if (uname == null || pwd == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        User user = userService.findUser(uname, pwd);
        if (user != null) {
            // 简化登录逻辑，直接返回用户信息（已移除Token）
            map.put("users", user);
            return Result.success(map);
        } else {
            return Result.error("用户名和密码错误");
        }
    }


    // 根据用户名查询用户信息 http://localhost:8081/findUserByUsername?username=admin
    @GetMapping("/findUserByUsername")
    public Result findUserByUsername(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        System.out.println(user);
        if (username != null){
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    // 注册接口 http://localhost:8081/register
    @PostMapping("/register")
    public Result register(User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    // 根据邮箱查询用户信息 http://localhost:8081/findUserByEmail?email=<EMAIL>
    @GetMapping("/findUserByEmail")
    public Result findUserByEmail(String email){
        User user = userService.findUserByEmail(email);
        if (user != null){
            return Result.success(user);
        }
        return Result.error("邮箱不存在");
    }
    // 发送邮箱验证码 http://localhost:8081/sendEmailCode?email=<EMAIL>
    @GetMapping("/sendEmailCode")
    public Result sendEmailCode(String email){
        User user = userService.findUserByEmail(email);
        if (user != null){
            userService.sendEmailCode(email);
            return Result.success("验证码发送成功");
        }
        return Result.notFound("邮箱不存在");
    }
    // 修改密码 http://localhost:8081/updatePasswordByEmail?email=<EMAIL>&password=<PASSWORD>&code=
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

    // 修改昵称 http://localhost:8081/updateNickname?user_id=1&nickname=
    @PutMapping("/updateNickname")
    public Result updateNickname(int user_id, String nickname) {
        int result = userService.updateNicknameById(user_id, nickname);
        if (result > 0) {
            return Result.success("昵称修改成功");
        } else {
            return Result.error("昵称修改失败");
        }
    }
}
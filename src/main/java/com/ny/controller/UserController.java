package com.ny.controller;

import com.ny.entity.Result;
import com.ny.entity.User;
import com.ny.service.UserService;
import com.ny.until.JwtUntil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关操作接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUntil jwtUntil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // 密码正则表达式（至少8位，包含字母和数字）
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");

    // 查询所有用户接口 http://localhost:8081/user/selectAllUser
    @GetMapping("/selectAllUser")
    @Operation(summary = "查询所有用户", description = "获取系统中的所有用户信息")
    public Result selectAllUser(){
        // 需要添加权限控制
        return Result.success(userService.findAllUser());
    }

    // 登录接口：POST /user/login (JSON请求体方式)
    //http://localhost:8081/user/login
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录")
    public Result login(@Valid @RequestBody User user) {
        // 输入验证
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        User foundUser = userService.findUserByUsername(user.getUsername());
        // 密码验证应该在Service层进行，这里只做简单的展示
        if (foundUser != null && userService.verifyPassword(user.getPassword(), foundUser.getPassword())) {
            String token = jwtUntil.createToken(foundUser.getId());
            redisTemplate.opsForValue().set("token:" + foundUser.getId(), token, 24, TimeUnit.HOURS);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", token);
            // 不返回敏感信息
            User safeUser = new User();
            safeUser.setId(foundUser.getId());
            safeUser.setUsername(foundUser.getUsername());
            safeUser.setNickname(foundUser.getNickname());
            safeUser.setEmail(foundUser.getEmail());
            responseMap.put("user", safeUser);
            return Result.success(responseMap);
        } else {
            // 使用通用错误信息，避免信息泄露
            return Result.error("用户名或密码错误");
        }
    }
    
    // 登录接口：POST /user/login1 (表单参数方式)
    @PostMapping("/login1")
    @Operation(summary = "用户登录(表单参数)", description = "通过表单参数提交用户名和密码进行登录")
    public Result login1(@Parameter(description = "用户名") @RequestParam String username, 
                         @Parameter(description = "密码") @RequestParam String password) {
        // 输入验证
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        User foundUser = userService.findUserByUsername(username);
        if (foundUser != null && userService.verifyPassword(password, foundUser.getPassword())) {
            String token = jwtUntil.createToken(foundUser.getId());
            redisTemplate.opsForValue().set("token:" + foundUser.getId(), token, 24, TimeUnit.HOURS);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", token);
            // 不返回敏感信息
            User safeUser = new User();
            safeUser.setId(foundUser.getId());
            safeUser.setUsername(foundUser.getUsername());
            safeUser.setNickname(foundUser.getNickname());
            safeUser.setEmail(foundUser.getEmail());
            responseMap.put("user", safeUser);
            return Result.success(responseMap);
        } else {
            return Result.error("用户名或密码错误");
        }
    }


    // 根据用户名查询用户信息 http://localhost:8081/user/findUserByUsername?username=admin
    @GetMapping("/findUserByUsername")
    @Operation(summary = "根据用户名查询用户", description = "通过用户名查找用户信息")
    public Result findUserByUsername(@Parameter(description = "用户名") @RequestParam String username) {
        // 需要添加权限控制或验证当前用户只能查询自己
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        User user = userService.findUserByUsername(username);
        if (user != null){
            // 不返回密码等敏感信息
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setUsername(user.getUsername());
            safeUser.setNickname(user.getNickname());
            safeUser.setEmail(user.getEmail());
            return Result.success(safeUser);
        }
        return Result.error("用户不存在");
    }

    // 注册接口 http://localhost:8081/user/register
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public Result register(@Valid @RequestBody User user) {
        // 输入验证
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || !PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            return Result.error("密码至少8位，必须包含字母和数字");
        }
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return Result.error("邮箱格式不正确");
        }
        
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    // 根据邮箱查询用户信息 http://localhost:8081/user/findUserByEmail?email=<EMAIL>
    @GetMapping("/findUserByEmail")
    @Operation(summary = "根据邮箱查询用户", description = "通过邮箱查找用户信息")
    public Result findUserByEmail(@Parameter(description = "用户邮箱") @RequestParam String email){
        // 需要添加权限控制
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return Result.error("邮箱格式不正确");
        }
        User user = userService.findUserByEmail(email);
        if (user != null){
            // 不返回密码等敏感信息
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setUsername(user.getUsername());
            safeUser.setNickname(user.getNickname());
            safeUser.setEmail(user.getEmail());
            return Result.success(safeUser);
        }
        return Result.error("用户不存在");
    }
    // 发送邮箱验证码 http://localhost:8081/user/sendEmailCode?email=<EMAIL>
    @PostMapping("/sendEmailCode")
    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送验证码")
    public Result sendEmailCode(@Parameter(description = "用户邮箱") @RequestParam String email){
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return Result.error("邮箱格式不正确");
        }
        
        User user = userService.findUserByEmail(email);
        if (user != null){
            // 检查是否频繁发送
            if (redisTemplate.hasKey("email:limit:" + email)) {
                return Result.error("发送过于频繁，请稍后再试");
            }
            
            userService.sendEmailCode(email);
            // 设置发送限制，1分钟内只能发送一次
            redisTemplate.opsForValue().set("email:limit:" + email, "1", 1, TimeUnit.MINUTES);
            return Result.success("验证码发送成功");
        }
        return Result.notFound("邮箱不存在");
    }
    // 修改密码 http://localhost:8081/user/updatePasswordByEmail?email=<EMAIL>&password=<PASSWORD>&code=
    @PostMapping("/updatePasswordByEmail")
    @Operation(summary = "通过邮箱修改密码", description = "通过邮箱验证码修改用户密码")
    public Result updatePasswordByEmail(@Parameter(description = "用户邮箱") @RequestParam String email, 
                                        @Parameter(description = "新密码") @RequestParam String password, 
                                        @Parameter(description = "验证码") @RequestParam String code){
        // 输入验证
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return Result.error("邮箱格式不正确");
        }
        if (code == null || code.length() != 6) {
            return Result.error("验证码格式不正确");
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            return Result.error("密码至少8位，必须包含字母和数字");
        }
        
        Boolean checkCode = userService.checkCode(email, code);
        if (checkCode){
            int i = userService.updatePasswordByEmail(email, password);
            if (i>0){
                // 删除验证码，防止重复使用
                redisTemplate.delete("email:" + email);
                return Result.success("修改成功");
            }
            return Result.error("修改密码失败");
        } else {
            return Result.error("验证码错误或已过期");
        }
    }

    // 修改昵称 http://localhost:8081/user/updateNickname?user_id=1&nickname=
    @PostMapping("/updateNickname")
    @Operation(summary = "修改用户昵称", description = "根据用户ID修改用户昵称")
    public Result updateNickname(@Parameter(description = "用户ID") @RequestParam Integer user_id, 
                                 @Parameter(description = "新昵称") @RequestParam String nickname) {
        // 需要验证当前用户只能修改自己的昵称
        if (user_id == null || nickname == null || nickname.trim().isEmpty()) {
            return Result.error("参数错误");
        }
        if (nickname.length() > 20) {
            return Result.error("昵称长度不能超过20个字符");
        }
        
        int result = userService.updateNicknameById(user_id, nickname);
        if (result > 0) {
            return Result.success("昵称修改成功");
        } else {
            return Result.error("昵称修改失败或用户不存在");
        }
    }
}
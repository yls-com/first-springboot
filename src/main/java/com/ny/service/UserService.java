package com.ny.service;

import com.ny.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {
    public List<User> findAllUser();

    // 登录方法声明
    public User login(String username, String password);

    public User findUserByUsername(String username);

    // 注册方法声明
    public boolean register(User user);

    User findUserByEmail(String email);

    // 发送邮箱验证码
    public void sendEmailCode(String email);

}

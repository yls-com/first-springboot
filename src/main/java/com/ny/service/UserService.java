package com.ny.service;

import com.ny.entity.User;

import java.util.List;

public interface UserService {
    public List<User> findAllUser();

    // 登录方法声明
    public User login(String username, String password);

    public User findUserByUsername(String username);
}

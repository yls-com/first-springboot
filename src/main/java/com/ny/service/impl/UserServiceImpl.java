package com.ny.service.impl;

import com.ny.entity.User;
import com.ny.mapper.UserMapper;
import com.ny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }

    // 实现登录方法
    @Override
    public User login(String username, String password) {
        return userMapper.findUser(username, password);
    }

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByName(username);
    }
}

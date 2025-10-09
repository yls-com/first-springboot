package com.ny.service.impl;

import com.ny.entity.User;
import com.ny.mapper.UserMapper;
import com.ny.service.UserService;
import com.ny.until.PasswordUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findUserByName(user.getUsername());
        if (existingUser != null) {
            return false; // 用户名已存在，注册失败
        }

        // 设置默认值
        user.setIs_active(1); // 默认激活状态
        user.setCreated_time(new Date()); // 设置创建时间

        //进行密码MD5加密
        String password = user.getPassword();
        String pwd = PasswordUntil.encryptPassword(password);
        user.setPassword(pwd);
        // 执行插入操作
        int result = userMapper.registertUser(user);
        return result > 0; // 插入成功返回true，否则返回false
    }

}
